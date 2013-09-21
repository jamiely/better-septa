(ns better-septa.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.java.jdbc :as sql]
            [clojure.data.json :as json]))

(defn haversine
  [{lon1 :longitude lat1 :latitude} {lon2 :longitude lat2 :latitude}]
  (let [radius 6372.8
        dlat (Math/toRadians (- lat2 lat1))
        dlon (Math/toRadians (- lon2 lon1))
        lat1 (Math/toRadians lat1)
        lat2 (Math/toRadians lat2)
        a (+ (* (Math/sin (/ dlat 2)) (Math/sin (/ dlat 2))) (* (Math/sin (/ dlon 2)) (Math/sin (/ dlon 2)) (Math/cos lat1) (Math/cos lat2)))]
    (* radius 2 (Math/asin (Math/sqrt a)))))

(defn route-stops-with-directions
  [route directions]
  (sql/with-connection (System/getenv "DATABASE_URL")
   (sql/with-query-results results
     ["SELECT * FROM route_stops WHERE route_short_name = ? AND direction_id IN (?) ORDER BY stop_sequence" (str route) directions]
     (into [] results))))

(declare route-stops)

(defn route-stops-with-direction
  [route direction]
  (route-stops route (seq direction)))

(defn route-stops-and-distance
  [route lat lng]
  (map (fn [stop]
         (assoc stop :distance (haversine {:longitude lng :latitude lat} {:longitude (:stop_lon stop) :latitude (:stop_lat stop)})))
    (route-stops route)))

(def route-stops #(route-stops-with-directions %1 [0 1]))

(defn nearest-stop
  [route lat lng]
  (first (sort-by :distance (route-stops-and-distance route lat lng))))

(defn route-stops-by-direction [stops]
  (partition-by :direction_id stops))

(defn stop-index [stops stop_id]
  "Given a list of stops and a stop_id, will return the index of the stop"
  (let [stop-pair (first (filter #(= (:stop_id (get %1 1)) stop_id)
                    (map-indexed vector stops)))]
    (get stop-pair 0)))

(defn intermediate-stops-given-stops
  [stops start-stop end-stop]
  (let [f-indices #(map (partial stop-index %1) [start-stop end-stop])
      indices (map f-indices (route-stops-by-direction stops))]
; [[2, 1], [1, 2]]
      indices
    ))

(defn intermediate-stops
  [route start-stop end-stop]
  (let [stops (route-stops route)]
    (intermediate-stops-given-stops stops start-stop end-stop)))

(defroutes app-routes
  (GET "/stops" {params :query-params} (json/write-str (nearest-stop (get params "route"), 39.920554, -75.150118)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
