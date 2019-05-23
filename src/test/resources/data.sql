/**
  Default Roles
 */
INSERT INTO roles (name)
VALUES ('ROLE_USER'),
       ('ROLE_ADMIN');

/**
  Default Plans
 */

INSERT INTO plans (name, price, duration, storagespace, CREATED_AT, UPDATED_AT)
values ('Free', 0, -1, 10,'2018-01-21T08:30:20', '2018-01-21T08:30:20');
values ('Paid', 10, 30, 50,'2018-01-21T08:30:20', '2018-01-21T08:30:20');