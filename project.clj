(defproject better-septa "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [postgresql "9.1-901.jdbc4"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [compojure "1.1.5"]
                 [org.clojure/data.json "0.2.3"]]
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler better-septa.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]]}})
