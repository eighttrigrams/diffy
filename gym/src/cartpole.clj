(ns cartpole
  (:require
    [runner :as runner]
    [physics :refer :all]))

(defn init []
  (create-body:rectangle [0 0 4 200] :rod)
  (translate-bodies [:rod] 200 -150)

  (create-body:rectangle [-150.0 -50 25 100] :pillar-left :infinite)
  (create-body:rectangle [150.0 -50 25 100] :pillar-right :infinite)
  (create-body:rectangle [-156.25 5 12.5 10] :pillar-leftmost :infinite)
  (create-body:rectangle [156.50 5 12.5 10] :pillar-rightmost :infinite)
  (translate-bodies
   [:pillar-leftmost :pillar-left :pillar-right :pillar-rightmost] 200 -300)

  (create-body:rectangle [0 0 300 2.0] :rail)

  (create-body:rectangle [0 0 30 10] :ship)
  (create-body:rectangle [-10 10 10 8] :constraint-left)
  (create-body:rectangle [10 10 10 8] :constraint-right)

  (create-joint:weld :weld1 :ship :constraint-left [-10.0 10.0])
  (create-joint:weld :weld2 :ship :constraint-right [10.0 10.0])

  (create-joint:prismatic :prismatic :rail :ship [-50.0 0.0] [50.0 0.0])
  (translate-bodies [:rail] 200 -304)
  (translate-bodies [:ship :constraint-left :constraint-right] 200 -304)
  {})

(def speed 10)

(defn on-tick-observer [notify-subscriber]
  (fn on-tick [state keys-pressed tick-in-ms]
    (cond
      (.contains keys-pressed \a)
      (set-motor-speed :prismatic speed)
      (.contains keys-pressed \d)
      (set-motor-speed :prismatic (- speed))
      :else (set-motor-speed :prismatic 0))
    (-> state
        (assoc :rotation (get-rotation :rod))
        (notify-subscriber))))

(defn go [on-tick-subscriber]
  (runner/run (init) (on-tick-observer on-tick-subscriber)))
