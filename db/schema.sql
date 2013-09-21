--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: route_stops; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE route_stops (
    route_id character varying,
    route_short_name character varying,
    direction_id integer,
    stop_name character varying,
    stop_sequence integer,
    stop_lat double precision,
    stop_lon double precision
);


--
-- Name: routes; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE routes (
    route_id character varying NOT NULL,
    route_short_name character varying,
    route_long_name character varying NOT NULL,
    route_type integer NOT NULL,
    route_color character varying,
    route_text_color character varying,
    route_url character varying
);


--
-- Name: stop_times; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE stop_times (
    trip_id character varying NOT NULL,
    arrival_time character varying NOT NULL,
    departure_time character varying NOT NULL,
    stop_id character varying NOT NULL,
    stop_sequence integer NOT NULL
);


--
-- Name: stops; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE stops (
    stop_id character varying NOT NULL,
    stop_name character varying NOT NULL,
    stop_lat double precision NOT NULL,
    stop_lon double precision NOT NULL,
    location_type integer,
    parent_station character varying,
    zone_id character varying,
    wheelchair_boarding character varying
);


--
-- Name: trips; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE trips (
    route_id character varying NOT NULL,
    service_id character varying NOT NULL,
    trip_id character varying NOT NULL,
    trip_headsign character varying,
    block_id character varying,
    direction_id integer,
    shape_id character varying
);


--
-- Name: route_stops_v; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW route_stops_v AS
 SELECT DISTINCT r.route_id, 
    r.route_short_name, 
    t.direction_id, 
    s.stop_name, 
    st.stop_sequence, 
    s.stop_lat, 
    s.stop_lon
   FROM (((routes r
   JOIN trips t ON (((t.route_id)::text = (r.route_id)::text)))
   JOIN stop_times st ON (((st.trip_id)::text = (t.trip_id)::text)))
   JOIN stops s ON (((s.stop_id)::text = (st.stop_id)::text)))
  ORDER BY t.direction_id, st.stop_sequence;


--
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM hector;
GRANT ALL ON SCHEMA public TO hector;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

