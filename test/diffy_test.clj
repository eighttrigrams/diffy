(ns diffy-test
  (:require [clojure.test :refer :all]
            [diffy.loss :as l]
            [diffy.activation :as activation]
            [diffy.helpers :as h]
            [diffy.dense :as layer]
            [diffy.net :as diffy]
            [diffy.matrix.matrix :refer :all]
            [diffy.matrix.clojure-matrix
             :refer  [impl]
             :rename {impl clojure-matrix-impl}]
;            [diffy.matrix.neander-matrix
;             :refer  [impl]
;             :rename {impl neander-matrix-impl}]
            [clojure.core.matrix :as ccm]
            [diffy.matrix.clojure-core-matrix
             :refer  [impl]
             :rename {impl clojure-core-matrix-impl}]))

(use-fixtures :once
              (fn [all-tests]
                (ccm/set-current-implementation :vectorz)
                (all-tests)))
(use-fixtures :each
              (fn [test]
                (choose-impl! clojure-matrix-impl)
                (test)
                (choose-impl! clojure-core-matrix-impl)
;                (test)
;                (choose-impl! neander-matrix-impl)
                (test)))

(def comparison-precision 1.0E-6)

;; The numbers are based on https://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/
;; There exists also a video from someone doing the calculations https://www.youtube.com/watch?v=0e0z28wAWfg

(def X [0.05 0.10])

(def Y [0.01 0.99])

(def W_H [[0.15 0.20] [0.25 0.30]])

(def W_O [[0.40 0.45] [0.50 0.55]])

(def b_H 0.35)

(def b_O 0.60)

(def g_W_H [[2.192E-4 4.385E-4] [2.488E-4 4.977E-4]])

(def g_W_O [[0.0410835 0.0413338] [-0.0113013 -0.0113702]])

(def g_bs_H [0.0043856, 0.0049771])

(def g_b_H (apply + g_bs_H))

(def g_bs_O [0.0692492, -0.0190492])

(def g_b_O (apply + g_bs_O))

(defn- network []
  (diffy/sequential
   [[(matrix W_H) (matrix b_H)]
    [(matrix W_O) (matrix b_O)]]
   2
   [(layer/dense false) activation/sigmoid 2]
   [(layer/dense false) activation/sigmoid 2]))

(defn- multi-b-network []
  (diffy/sequential
   [[(matrix W_H) (matrix [b_H b_H])]
    [(matrix W_O) (matrix [b_O b_O])]]
   2
   [(layer/dense) activation/sigmoid 2]
   [(layer/dense) activation/sigmoid 2]))

(defn- get-gradients [[_ initial-weights] [_ updated-weights]]
  (mapv
   (fn [weights0 weights1]
     [(sub (first weights0) (first weights1))
      (sub (second weights0) (second weights1))])
   initial-weights updated-weights))

(defn- train-and-get-gradients [network mini-batch]
  (get-gradients
   network
   (diffy/train network
                l/loss
                0.5
                [mini-batch])))

(defn- compare-predictions [preds1 preds2 precision]
  (every? #(< (Math/abs %) precision) (mapv #(- %1 %2) (to-clj preds1) preds2)))

(defn- compare-layers [layers1 layers2 precision]
  (every? true?
          (mapv
           (fn [[W0 b0] [W1 b1]]
             (let [diff-W  (vec (flatten (to-clj (sub W0 W1))))
                   diff-b  (to-clj (sub b0 b1))
                   diff-b  (if (number? diff-b) [diff-b] (vec (flatten diff-b)))
                   entries (vec (concat diff-W diff-b))]
               (every? #(< (Math/abs %) precision) entries)))
           layers1 layers2)))

(deftest test-predict
  (is
   (compare-predictions
    (diffy/predict (network) (matrix X))
    [0.751365 0.772928] comparison-precision)))

(deftest test-train
  (let [mini-batch-row   [(matrix X) (matrix Y)]
        result-1         [[(matrix g_W_H) (matrix g_b_H)]
                          [(matrix g_W_O) (matrix g_b_O)]]
        result-2         (h/multiply-dense-layers 2 result-1)]
    (is
     (compare-layers
      (train-and-get-gradients (network) [mini-batch-row])
      result-1
      comparison-precision))
    (is
     (compare-layers
      (train-and-get-gradients (network) [mini-batch-row mini-batch-row])
      result-2
      comparison-precision))))

(deftest test-multiple-b-values
  (let [mini-batch-row   [(matrix X) (matrix Y)]]
    (is
     (compare-layers (train-and-get-gradients (multi-b-network) [mini-batch-row])
                     [[(matrix g_W_H)
                       (matrix g_bs_H)]
                      [(matrix g_W_O)
                       (matrix g_bs_O)]]
                     comparison-precision))))
