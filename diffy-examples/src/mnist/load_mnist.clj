(ns mnist.load-mnist
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [diffy.matrix.matrix :refer :all]))

(defn go []
  (with-open [reader (io/reader "resources/mnist_784_csv.csv")]
    (let [mnist (mapv
                 (fn [row]
                   [(matrix (mapv (fn [x] (/ (Float/parseFloat x) 255.0)) (drop-last row)))
                    (matrix
                      (assoc
                       (vec (repeat 10 0.0))
                       (Integer/parseInt (get row 784)) 1.0))])
                 (doall
                  (drop 1 (csv/read-csv reader))))]
      [(take 60000 mnist)
       (drop 60000 mnist)])))

