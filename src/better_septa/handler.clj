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

(defn route-stops
  [route]
  (sql/with-connection (System/getenv "DATABASE_URL")
   (sql/with-query-results results
     ["SELECT * FROM route_stops WHERE route_short_name = ? AND direction_id = ? ORDER BY stop_sequence" (str route) 0]
     (into [] results))))

(defn route-stops-with-distance
  [route lat lng]
  (map (fn [stop]
         (assoc stop :distance (haversine {:longitude lng :latitude lat} {:longitude (:stop_lon stop) :latitude (:stop_lat stop)})))
    (route-stops route)))

(defn nearest-stop
  [route lat lng]
  (first (sort-by :distance (route-stops-with-distance route lat lng))))

(defroutes app-routes
  (GET "/stops" {params :query-params} (json/write-str (nearest-stop (get params "route"), 39.920554, -75.150118)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
