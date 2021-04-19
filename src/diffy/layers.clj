(ns diffy.layers)

(defn- construct
  ([prev-layer X forward calc-backward-e]
   (construct prev-layer X forward calc-backward-e nil))
  ([prev-layer X forward calc-backward-e calc-backward-g]
   (let [[I backward] (prev-layer X)
         O            (forward I)]
     [O
      (fn [E]
        (let [gradients (backward (calc-backward-e I O E))]
          (if (nil? calc-backward-g)
            gradients
            (calc-backward-g I O E gradients))))])))

(defn loss-layer [backward-e]
  (fn [prev-layer X Y]
    (second
     (construct prev-layer X identity
                (fn [I _O _E] (backward-e I Y))))))

(defn activation-layer [forward backward-e]
  (fn [prev-layer]
    (fn [X]
      (construct prev-layer X forward backward-e))))

(defn layer [forward backward-g backward-e]
  (fn [layer-weights prev-layer]
    (let [forward (partial forward layer-weights)]
      (fn [X]
        (construct prev-layer X forward
                   (fn [_I _O E] (backward-e layer-weights E))
                   (fn [I _O E gradients]
                     (conj gradients (backward-g layer-weights E I))))))))

(def terminate
  (fn [X] [X (fn [_E] [])]))

(defn connect-layers [[layers weights]]
  (reduce
   (fn [prev-layer [[layer activation-layer] layer-weights]]
     (activation-layer (layer layer-weights prev-layer)))
   terminate
   (map vector layers weights)))
