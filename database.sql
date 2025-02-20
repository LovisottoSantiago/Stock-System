--
-- PostgreSQL database dump
--

-- Dumped from database version 16.6 (Ubuntu 16.6-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.6 (Ubuntu 16.6-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: detallefactura; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.detallefactura (
    id integer NOT NULL,
    cantidad integer NOT NULL,
    preciounitario numeric(10,2) NOT NULL,
    subtotal numeric(10,2) NOT NULL,
    factura_id integer,
    producto_id bigint NOT NULL
);


ALTER TABLE public.detallefactura OWNER TO postgres;

--
-- Name: detallefactura_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.detallefactura_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.detallefactura_id_seq OWNER TO postgres;

--
-- Name: detallefactura_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.detallefactura_id_seq OWNED BY public.detallefactura.id;


--
-- Name: factura; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.factura (
    id integer NOT NULL,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    montofinal numeric(10,2) NOT NULL,
    tipo character varying(50) DEFAULT 'efectivo'::character varying NOT NULL,
    cliente character varying(255) DEFAULT 'Lovi'::character varying NOT NULL
);


ALTER TABLE public.factura OWNER TO postgres;

--
-- Name: factura_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.factura_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.factura_id_seq OWNER TO postgres;

--
-- Name: factura_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.factura_id_seq OWNED BY public.factura.id;


--
-- Name: producto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.producto (
    id bigint NOT NULL,
    titulo character varying(255) NOT NULL,
    categoria character varying(255) NOT NULL,
    cantidad integer NOT NULL,
    precio numeric(10,2) NOT NULL,
    imagen_url text
);


ALTER TABLE public.producto OWNER TO postgres;

--
-- Name: producto_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.producto_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.producto_id_seq OWNER TO postgres;

--
-- Name: producto_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.producto_id_seq OWNED BY public.producto.id;


--
-- Name: detallefactura id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detallefactura ALTER COLUMN id SET DEFAULT nextval('public.detallefactura_id_seq'::regclass);


--
-- Name: factura id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.factura ALTER COLUMN id SET DEFAULT nextval('public.factura_id_seq'::regclass);


--
-- Data for Name: detallefactura; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.detallefactura (id, cantidad, preciounitario, subtotal, factura_id, producto_id) FROM stdin;
145	2	10000.00	20000.00	51	1233
147	1	15000.00	15000.00	52	10
148	1	2600.00	2600.00	52	4
152	3	2400.00	7200.00	53	14
153	3	500.00	1500.00	54	6
154	2	800.00	1600.00	54	1
186	2	2200.00	4400.00	55	7793253005153
191	2	2200.00	4400.00	56	7793253005153
192	1	2000.00	2000.00	56	7791293044477
193	1	3800.00	3800.00	56	13
201	2	600.00	1200.00	57	6
202	2	3800.00	7600.00	58	13
203	3	13500.00	40500.00	58	12
204	1	10000.00	10000.00	58	1233
211	2	2200.00	4400.00	59	7793253005153
212	1	3800.00	3800.00	59	13
216	2	2400.00	4800.00	60	14
\.


--
-- Data for Name: factura; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.factura (id, fecha, montofinal, tipo, cliente) FROM stdin;
51	2025-02-12 14:24:56.222884	20000.00	Transferencia	Claudio
52	2025-02-12 14:27:17.343796	17600.00	Efectivo	Leonardo
53	2025-02-15 19:22:28.870504	7200.00	Transferencia	Luis VC
54	2025-02-15 19:27:07.688609	3100.00	Efectivo	Pepe
55	2025-02-19 19:31:00.957474	4400.00	Efectivo	Pedro
56	2025-02-19 22:36:57.420749	10200.00	Efectivo	Cacho
57	2025-02-19 22:38:05.775502	1200.00	Transferencia	Raul
58	2025-02-21 10:59:45.827222	58100.00	Efectivo	Pepe
59	2025-02-21 17:01:26.836309	8200.00	Efectivo	Liliana
60	2025-02-21 17:02:13.243867	4800.00	Transferencia	Silvia
\.


--
-- Data for Name: producto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.producto (id, titulo, categoria, cantidad, precio, imagen_url) FROM stdin;
1	Lavandina 2L	Liquidos sueltos	13	800.00	https://i.imgur.com/kyvoiTq.jpeg
10	New Pel 30 x 80m	Papel Higienico	12	15000.00	https://i.imgur.com/XaiFTwm.jpeg
5	Jabon p/ropa 5L	Liquidos sueltos	10	5000.00	https://i.imgur.com/YMqppkU.jpeg
1231	Clarificante Mak 1L	Pileta	12	9000.00	https://i.imgur.com/6qp3DFF.jpeg
1232	Cloro granulado rapido 1kg	Pileta	12	9000.00	https://i.imgur.com/43gUFr5.jpeg
2	Lavandina 5L	Liquidos sueltos	6	2000.00	https://i.imgur.com/KBkdbvM.jpeg
1234	Alguicida Mak 1L	Pileta	8	9000.00	https://i.imgur.com/lPRK2Mk.jpeg
3	Jabon p/ropa 1L	Liquidos sueltos	4	1300.00	https://i.imgur.com/AJb2kWV.jpeg
4	Jabon p/ropa 2L	Liquidos sueltos	8	2600.00	https://i.imgur.com/xrK7MVi.jpeg
7791293044477	Talco Rexona	Perfumeria	11	2000.00	https://dcdn.mitiendanube.com/stores/001/108/127/products/rexona-efficient-100g-byb1-8a876e45cb495a31cb15894586794641-640-0.png
6	Esponja salvauña	Bazar	6	600.00	https://i.imgur.com/AXKMstI.jpeg
12	Las lomitas 30 x 100m	Papel Higienico	7	13500.00	https://i.imgur.com/ebwzLjP.jpeg
1233	Cloro granulado lento 1kg	Pileta	9	10000.00	https://i.imgur.com/d7IyQ8g.jpeg
7793253005153	Selton moscas	Perfumeria	4	2200.00	https://jumboargentina.vtexassets.com/arquivos/ids/681582/Ins-selton-Rojo-Mmm-Acc-Instan-360-2-876567.jpg?v=637752765648830000
13	Cloro 5L	Liquidos sueltos	11	3800.00	https://i.imgur.com/ET52A3q.jpeg
14	Esponja dobleuso dorada	Bazar	10	2400.00	https://i.imgur.com/2eN8V5d.jpeg
\.


--
-- Name: detallefactura_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.detallefactura_id_seq', 220, true);


--
-- Name: factura_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.factura_id_seq', 60, true);


--
-- Name: producto_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.producto_id_seq', 1, false);


--
-- Name: detallefactura detallefactura_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detallefactura
    ADD CONSTRAINT detallefactura_pkey PRIMARY KEY (id);


--
-- Name: factura factura_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.factura
    ADD CONSTRAINT factura_pkey PRIMARY KEY (id);


--
-- Name: producto producto_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.producto
    ADD CONSTRAINT producto_pkey PRIMARY KEY (id);


--
-- Name: detallefactura fk_factura; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detallefactura
    ADD CONSTRAINT fk_factura FOREIGN KEY (factura_id) REFERENCES public.factura(id);


--
-- Name: detallefactura fk_producto; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detallefactura
    ADD CONSTRAINT fk_producto FOREIGN KEY (producto_id) REFERENCES public.producto(id);


--
-- PostgreSQL database dump complete
--

