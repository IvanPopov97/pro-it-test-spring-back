create table companies
(
	id bigserial
		constraint company_pk
			primary key,
	name text not null,
	head_company_id bigint
);

create unique index company_name_uindex
	on company (name);

create table employees
(
	id bigserial
		constraint employee_pk
			primary key,
	name text not null,
	company_id bigint,
	boss_id bigint
);


-- удалять компании, у которых есть дочерние, нельзя
alter table company
	add constraint company_company__fk
		foreign key (head_company_id) references company
			on update restrict on delete restrict;

-- удалять работников, у которых есть дочерние, нельзя
alter table employee
	add constraint employee_employee__fk
		foreign key (boss_id) references employee(id)
			on delete restrict on update restrict;

-- после удаления руководителя, у всех подчинённых id руководителя is null
alter table employee
	add constraint employee_company__fk
		foreign key (company_id) references company
			on update set null on delete set null;

create or replace function boss_or_company_update() returns trigger as $boss_or_company_update$
begin
    if new.boss_id is null or new.company_id is null then
        return new;
    end if;
    if coalesce(new.company_id != (select company_id from employee where id = new.boss_id), true) then
        raise exception 'Руководитель сотрудника должен работать в той же организации';
    end if;
    return new;
end
$boss_or_company_update$ language plpgsql;

create trigger boss_or_company_update
    before insert or update on employee
    for each row
    execute procedure boss_or_company_update();

--если занулили или изменили company_id у работника, то у всех его подчинённых зануляется boss_id
--применимо, когда работник уволился или поменял место работы
create or replace function company_update() returns trigger as $company_update$
begin
    if old.company_id is null then
        return new;
    end if;
    if coalesce(new.company_id != old.company_id, true) then
        execute 'update employee set boss_id=null where boss_id=$1 and company_id is not null' using new.id;
    end if;
    return new;
end
$company_update$ language plpgsql;

create trigger company_update
    before update on employee
    for each row
execute procedure company_update();