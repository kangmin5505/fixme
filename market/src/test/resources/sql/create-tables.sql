create table if not exists MARKET (
    id int primary key,
    clientId int not null,
    instrument varchar(255) not null,
    price int not null,
    quantity int not null
);