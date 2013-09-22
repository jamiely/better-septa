(ns better-septa.test.handler
  (:use clojure.test
        ring.mock.request
        better-septa.handler))

(deftest test-logic
  (testing "slicing"
    (is (= [2 3 4] (slice-between [1 2 3 4 5] 1 3))))
  (testing "stop-index"
    (let [stops [{:stop_id 9} {:stop_id 8} {:stop_id 7}]
          stops-with-dir (fn [dir] 
                           (let [items (map (fn [stop] 
                                              (assoc stop :direction_id dir)) stops)]
                             (if (= dir 0) items (reverse items))))]
      (is (= (stop-index stops 9) 0))
      (is (= (stop-index stops 8) 1))
      (is (= (stop-index stops 7) 2))
      (is (= (stop-indices stops [7 8 9]) [2 1 0]))
      (is (= (stop-indices-by-group [stops (reverse stops)] [7 8 9])
             [{:indices [2 1 0] :stops stops} 
              {:indices [0 1 2] :stops (reverse stops)}]))
      (is (= (stop-indices-by-proper-direction [stops (reverse stops)] [7 9])
             {:indices [0 2] :stops (reverse stops)}))
      (is (= (intermediate-stops-given-stops (flatten (map stops-with-dir [0 1])) 7 9)
             (stops-with-dir 1)))))
  (testing "in-order"
    (is (items-in-order [1 2 3]))
    (is (not (items-in-order [1 3 2])))))

