create table if not exists MARKET (
    id int primary key auto_increment,
    clientId char(6) not null,
    instrument varchar(255) not null,
    price int not null,
    quantity int not null
);