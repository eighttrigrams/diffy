(ns diffy.activation-test
  (:require [clojure.test :refer :all]
            [diffy.activation :refer :all]))

(deftest test-relu
  (let [activate      (relu
                       (def terminate
                         (fn [X]
                           [X
                            (fn [E] (is (= [2.0 0.0] E)))])))
        [O, backward] (activate [1.0 -1.0])
        E             (backward [2.0 2.0])]
    (is (= [1.0 0.0] O))))
