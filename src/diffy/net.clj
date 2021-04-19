(ns diffy.net
  (:require [diffy.helpers :as h]
            [diffy.layers :as l]
            [diffy.matrix.matrix :as m]))

(defn predict [net X]
  (first
   ((l/connect-layers net) X)))

(defn- sum-layer-gradients
  "Works for dense layer weights in the form of [W b]"
  [& layer-gradients]
  [(apply m/madd (map first layer-gradients))
   (apply m/madd (map second layer-gradients))])

(defn- calc-difference [weights gradients]
  (mapv
   (fn [[W0 b0] [W1 b1]]
     [(m/sub W0 W1)
      (m/sub b0 b1)])
   weights gradients))

(defn calc-mini-batch-gradients [loss-function learning-rate]
  (fn [[layers weights :as net] mini-batch]
    (let [prev-layer                    (l/connect-layers net)
          to-gradients                  (fn [[X Y]] ((loss-function prev-layer X Y) nil))
          updated-weights               (->>
                                         mini-batch
                                         (pmap to-gradients)
                                         (apply mapv sum-layer-gradients)
                                         (h/multiply-dense-layers learning-rate)
                                         (calc-difference weights))]
      [layers updated-weights])))

(defn train
  "Returns [layers update-weights] after going through all the given mini-batches
  "
  [net loss-function learning-rate mini-batches]
  (reduce (calc-mini-batch-gradients loss-function learning-rate)
          net
          mini-batches))

(defn sequential [initialize-weights n-inputs & layer-configs]
  (let [weights-passed-directly (vector? initialize-weights)]
    (second
     (->> layer-configs
          (reduce
           (fn [[n-in [layers weights]] layer-config]
             (let [[layer activation n-out] layer-config]
               [n-out
                [(concat layers [[(:layer layer) activation]])
                 (if weights-passed-directly
                   weights
                   (concat weights
                           [(initialize-weights ((:weights layer) n-in n-out))]))]]))
           [n-inputs
            [[] (if weights-passed-directly initialize-weights [])]])))))
