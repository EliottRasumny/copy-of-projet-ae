--Creation of the DB
DROP SCHEMA IF EXISTS project CASCADE;
CREATE SCHEMA project;

CREATE TABLE project.addresses
(
    id_address      SERIAL PRIMARY KEY NOT NULL,
    street          VARCHAR(100)       NOT NULL,
    building_number VARCHAR(5)         NOT NULL,
    unit_number     VARCHAR(4),
    postcode        VARCHAR(4)         NOT NULL,
    commune         VARCHAR(50)        NOT NULL,
    version_address INTEGER            NOT NULL
);

CREATE TABLE project.users
(
    id_user        SERIAL PRIMARY KEY NOT NULL,
    role           VARCHAR(6),
    username       VARCHAR(50)        NOT NULL,
    lastname       VARCHAR(50)        NOT NULL,
    surname        VARCHAR(50)        NOT NULL,
    address        INTEGER            NOT NULL REFERENCES project.addresses (id_address),
    phone_number   VARCHAR(25),
    password       VARCHAR(60)        NOT NULL,
    refusal_reason VARCHAR(257),
    state          VARCHAR(11)        NOT NULL,
    version        INTEGER            NOT NULL
);

CREATE TABLE project.types
(
    id_type SERIAL PRIMARY KEY NOT NULL,
    label   VARCHAR(50)        NOT NULL
);

CREATE TABLE project.objects
(
    id_object   SERIAL PRIMARY KEY NOT NULL,
    type        INTEGER            NOT NULL REFERENCES project.types (id_type),
    description VARCHAR(500)       NOT NULL,
    picture     VARCHAR(100),
    time_slot   VARCHAR(500)       NOT NULL,
    offeror     INTEGER            NOT NULL REFERENCES project.users (id_user),
    recipient   INTEGER REFERENCES project.users (id_user),
    state       VARCHAR(10)        NOT NULL,
    version     INTEGER            NOT NULL
);

CREATE TABLE project.offers
(
    id_offer   SERIAL PRIMARY KEY NOT NULL,
    object     INTEGER            NOT NULL REFERENCES project.objects (id_object),
    offer_date TIMESTAMP          NOT NULL,
    state      VARCHAR(11)        NOT NULL
);

CREATE TABLE project.ratings
(
    id_rating SERIAL PRIMARY KEY NOT NULL,
    object    INTEGER            NOT NULL REFERENCES project.objects (id_object),
    detail    VARCHAR(150)       NOT NULL,
    value     INTEGER            NOT NULL
);

CREATE TABLE project.interests
(
    id_interest       SERIAL PRIMARY KEY NOT NULL,
    offer             INTEGER            NOT NULL REFERENCES project.offers (id_offer),
    interested_member INTEGER            NOT NULL REFERENCES project.users (id_user),
    date              VARCHAR(500)       NOT NULL,
    answers_call      BOOLEAN            NOT NULL,
    recipient_chosen  BOOLEAN,
    has_come          BOOLEAN,
    read              BOOLEAN            NOT NULL
);

-- Ajouter Caroline
INSERT INTO project.addresses
VALUES (DEFAULT, 'Rue de l''Eglise', '11', 'B1', '4987', 'Stoumont', 1);
INSERT INTO project.users
VALUES (DEFAULT, 'admin', 'caro', 'Line', 'Caroline', 1, NULL,
        '$2a$12$34AIKO0nOpfk68YctGWHa.cJMeqw/P/eLEZ6xUDprCwVDz9OWtqLS', NULL
           , 'valid', 1);

-- Add Achille
INSERT INTO project.addresses
VALUES (DEFAULT, 'Rue de Renkin', '7', NULL, '4800', 'Verviers', 1);
INSERT INTO project.users
VALUES (DEFAULT, NULL, 'achil', 'Ile', 'Achille', 2, NULL,
        '$2a$12$34AIKO0nOpfk68YctGWHa.cJMeqw/P/eLEZ6xUDprCwVDz9OWtqLS',
        'L''application n''est pas encore ouverte à tous.', 'denied', 1);

-- Add Basile
INSERT INTO project.addresses
VALUES (DEFAULT, 'Rue Haute Folie', '6', 'A103', '4800', 'Verviers', 1);
INSERT INTO project.users
VALUES (DEFAULT, 'member', 'bazz', 'Ile', 'Basile', 3, NULL,
        '$2a$12$34AIKO0nOpfk68YctGWHa.cJMeqw/P/eLEZ6xUDprCwVDz9OWtqLS', NULL, 'valid', 1);

-- Add Brigitte
INSERT INTO project.addresses
VALUES (DEFAULT, 'Haut-Vinâve', '13', NULL, '4845', 'Jalhay', 1);
INSERT INTO project.users
VALUES (DEFAULT, 'admin', 'bri', 'Lehmann', 'Brigitte', 4, NULL,
        '$2a$12$/WXYAERRHOjgQG4yEV1nmuAe5ZGRth3Sw5rtoxUu3jtSjce8cdEeC', NULL, 'valid', 1);

--Add Théophile
INSERT INTO project.addresses
VALUES (DEFAULT, 'Rue de Renkin', '7', NULL, '4800', 'Verviers', 1);
INSERT INTO project.users
VALUES (DEFAULT, 'member', 'theo', 'Ile', 'Théophile', 5, NULL,
        '$2a$12$GLHy6eIdulC7WVNXG.rE.eWnJ2COG6P2nFrm2/QtaNHiuvgeJ2iWa', NULL, 'valid', 1);

--Add Emile
INSERT INTO project.addresses
VALUES (DEFAULT, 'Rue de Verviers', '47', NULL, '4000', 'Liege', 1);
INSERT INTO project.users
VALUES (DEFAULT, NULL, 'emi', 'Ile', 'Emile', 6, NULL,
        '$2a$12$4MKL.WG80gAXX3GvGnhBbuApPqkT.VzQI9Hsi2VH20OUHRbX/P7na',
        'L''application n''est pas encore ouverte à tous.', 'denied', 1);

--Add Coralie
INSERT INTO project.addresses
VALUES (DEFAULT, 'Rue du salpêtré', '789', 'Bis', '1040', 'Bruxelles', 1);
INSERT INTO project.users
VALUES (DEFAULT, NULL, 'cora', 'Line', 'Coralie', 7, NULL,
        '$2a$12$HgCCizHsdrw49Qz3dMGAPu8rbq7oE16aN5g6VaPVp8rSvWc0FzzES',
        'Vous devez encore attendre quelques jours.', 'denied', 1);


--Add Charles
INSERT INTO project.addresses
VALUES (DEFAULT, 'Rue des Minières', '45', 'Ter', '4800', 'Verviers', 1);
INSERT INTO project.users
VALUES (DEFAULT, NULL, 'charline', 'Line', 'Charles', 8, NULL,
        '$2a$12$kvRK2SC/FH1ZeXiT.ZR/3.LdeTsYMKb9dlruH.z9Hl8FXJ0pLLs02',
        null, 'registered', 1);

-- Add all types
INSERT INTO project.types
VALUES (DEFAULT, 'Accessoires pour animaux domestiques');
INSERT INTO project.types
VALUES (DEFAULT, 'Accessoires pour voiture');
INSERT INTO project.types
VALUES (DEFAULT, 'Décoration');
INSERT INTO project.types
VALUES (DEFAULT, 'Jouets');
INSERT INTO project.types
VALUES (DEFAULT, 'Literie');
INSERT INTO project.types
VALUES (DEFAULT, 'Matériel de cuisine');
INSERT INTO project.types
VALUES (DEFAULT, 'Matériel de jardinage');
INSERT INTO project.types
VALUES (DEFAULT, 'Meuble');
INSERT INTO project.types
VALUES (DEFAULT, 'Plantes');
INSERT INTO project.types
VALUES (DEFAULT, 'Produits cosmétiques');
INSERT INTO project.types
VALUES (DEFAULT, 'Vélo, trottinette ');
INSERT INTO project.types
VALUES (DEFAULT, 'Vêtements');

-- Add object 1
INSERT INTO project.objects
VALUES (DEFAULT, 3, 'Décorations de Noël de couleur rouge', 'christmas-1869533_640.png',
        'Mardi de 17h à 22h', 3, NULL,
        'canceled', 1);

-- Add offer 1
INSERT INTO project.offers
VALUES (DEFAULT, 1, '2022-03-21', 'outdated');

-- Add object 2
INSERT INTO project.objects
VALUES (DEFAULT, 3, 'Cadre représentant un chien noir sur un fond noir.', 'dog-4118585_640.jpg',
        'Lundi de 18h à 22h', 3, NULL, 'donated', 1);

-- Add offer 2
INSERT INTO project.offers
VALUES (DEFAULT, 2, '2022-03-25', 'available');

-- Add object 3
INSERT INTO project.objects
VALUES (DEFAULT, 8, 'Ancien bureau d''écolier.', 'BureauEcolier-7.JPG',
        'Tous les jours de 15h à 18h', 4, NULL,
        'assignable', 1);

-- Add offer 3
INSERT INTO project.offers
VALUES (DEFAULT, 3, '2022-03-25', 'available');


-- Add object 4
INSERT INTO project.objects
VALUES (DEFAULT, 7, 'Brouette à deux roues à l’avant. Améliore la stabilité et ne
fatigue pas le dos.', 'wheelbarrows-4566619_640.jpg', 'Tous les matins avant 11h30', 5, NULL,
        'assignable', 1);

-- Add offer 4
INSERT INTO project.offers
VALUES (DEFAULT, 4, '2022-03-28', 'available');

-- Add object 5
INSERT INTO project.objects
VALUES (DEFAULT, 7, 'Scie sur perche Gardena.', NULL, 'Tous les jours de 15h à 18h', 4, NULL,
        'donated', 1);

-- Add offer 5
INSERT INTO project.offers
VALUES (DEFAULT, 5, '2022-03-28', 'available');

-- Add object 6
INSERT INTO project.objects
VALUES (DEFAULT, 8, 'Table jardin et deux chaises en bois.', 'table-jardin.jpg',
        'Tous les jours de 15h à 18h', 4,
        NULL, 'donated', 1);

-- Add offer 6
INSERT INTO project.offers
VALUES (DEFAULT, 6, '2022-03-29', 'available');

-- Add object 7
INSERT INTO project.objects
VALUES (DEFAULT, 8, 'Table bistro.', 'table-bistro.jpg', 'Lundi de 18h à 20h', 5,
        NULL, 'donated', 1);

-- Add offer 7
INSERT INTO project.offers
VALUES (DEFAULT, 7, '2022-03-30', 'available');

-- Add object 8
INSERT INTO project.objects
VALUES (DEFAULT, 8, 'Table bistro ancienne de couleur bleue.', 'table-bistro-carree-bleue.jpg', 'Samedi en
journée', 1, NULL, 'assignable', 1);

-- Add offer 8
INSERT INTO project.offers
VALUES (DEFAULT, 8, '2022-04-14', 'available');

-- Add object 9
INSERT INTO project.objects
VALUES (DEFAULT, 4, 'Tableau noir pour enfant.', 'tableau.jpg', 'Lundi de 18h à 20h', 1, 1,
        'assigned', 1);

-- Add offer 9
INSERT INTO project.offers
VALUES (DEFAULT, 9, '2022-04-14', 'available');

-- Add object 10
INSERT INTO project.objects
VALUES (DEFAULT, 3, 'Cadre cottage naïf.', 'cadre-cottage-1178704_640.jpg', 'Lundi de 18h30 à 20h',
        5, NULL, 'assignable', 1);

-- Add offer 10
INSERT INTO project.offers
VALUES (DEFAULT, 10, '2022-04-21', 'available');

-- Add object 11
INSERT INTO project.objects
VALUES (DEFAULT, 6, 'Tasse de couleur claire rose & mauve.', 'tasse-garden-5037113_640.jpg',
        'Lundi de 18h30 à 20h', 5, NULL, 'assignable', 1);

-- Add offer 11
INSERT INTO project.offers
VALUES (DEFAULT, 11, '2022-04-21', 'available');

-- Add object 12
INSERT INTO project.objects
VALUES (DEFAULT, 9, 'Pâquerettes dans pots rustiques.', 'pots-daisy-181905_640.jpg',
        'Lundi de 16h à 17h', 1, 3, 'assigned', 1);

-- Add offer 12
INSERT INTO project.offers
VALUES (DEFAULT, 12, '2022-04-21', 'available');

-- Add object 13
INSERT INTO project.objects
VALUES (DEFAULT, 9, 'Pots en grès pour petites plantes.', 'pots-plants-6520443_640.jpg',
        'Lundi de 16h à 17h', 1, NULL, 'assignable', 1);

-- Add offer 13
INSERT INTO project.offers
VALUES (DEFAULT, 13, '2022-04-21', 'available');


-- Add interest 1

INSERT INTO project.interests
VALUES (DEFAULT, 3, 5, '16 MAI', false, null, null,true);

-- Add interest 2

INSERT INTO project.interests
VALUES (DEFAULT, 3, 3, '17 MAI', false, null, null,true);

-- Add interest 3

INSERT INTO project.interests
VALUES (DEFAULT, 4, 3, 'Le 10 mai', false, null, null,true);

-- Add interest 4

INSERT INTO project.interests
VALUES (DEFAULT, 4, 4, 'Le 10 mai', false, null, null,true);

-- Add interest 5

INSERT INTO project.interests
VALUES (DEFAULT, 4, 1, 'Le 10 mai', false, null, null,true);

-- Add interest 6

INSERT INTO project.interests
VALUES (DEFAULT, 8, 5, '14 mai', false, null, null,true);

-- Add interest 7

INSERT INTO project.interests
VALUES (DEFAULT, 8, 4, '14 mai', false, null, null,true);

-- Add interest 8

INSERT INTO project.interests
VALUES (DEFAULT, 9, 1, '16 mai', false, true, null,true);

-- Add interest 9

INSERT INTO project.interests
VALUES (DEFAULT, 10, 1, 'Le 10 mai', false, null, null,true);

-- Add interest 10

INSERT INTO project.interests
VALUES (DEFAULT, 10, 3, 'Le 10 mai', false, null, null,true);

-- Add interest 11

INSERT INTO project.interests
VALUES (DEFAULT, 10, 4, 'Le 10 mai', false, null, null,true);

-- Add interest 12

INSERT INTO project.interests
VALUES (DEFAULT, 11, 3, 'Le 16 mai', false, null, null,true);

-- Add interest 13

INSERT INTO project.interests
VALUES (DEFAULT, 11, 1, 'Le 16 mai', false, null, null,true);

-- Add interest 14

INSERT INTO project.interests
VALUES (DEFAULT, 12, 3, 'Le 16 mai', false, true, null,true);


SELECT us.id_user, us.username, us.role, us.state, us.refusal_reason
FROM project.users us
ORDER BY us.role, us.state;

SELECT ob.description, ty.label, ob.state, of.offer_date
FROM project.objects ob,
     project.types ty,
     project.offers of
WHERE ob.type = ty.id_type
  AND ob.id_object = of.object
ORDER BY of.offer_date DESC;

SELECT us.lastname, count(ob.id_object)
FROM project.objects ob,
     project.users us
WHERE ob.offeror = us.id_user
GROUP BY us.lastname;


SELECT ob.description, ty.label, ob.state, us.lastname
FROM project.objects ob,
     project.types ty,
     project.users us
WHERE ob.type = ty.id_type
  AND ob.state = 'assigned'
  AND ob.recipient = us.id_user;

SELECT ob.state, count(ob.state)
FROM project.objects ob
GROUP BY ob.state;
