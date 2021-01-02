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

  (create-joint:weld :ship :constraint-left [-10.0 10.0])
  (create-joint:weld :ship :constraint-right [10.0 10.0])

  ;; TODO add user-data to joint and access it by symbol
  (let [joint (create-joint:prismatic :rail :ship [-50.0 0.0] [50.0 0.0])]
    (translate-bodies [:rail] 200 -304)
    (translate-bodies [:ship :constraint-left :constraint-right] 200 -304)
    {:joint-ref joint}))

(def speed 10)

(defn on-tick [notify {joint :joint-ref :as state} keys-pressed tick-in-ms]
  (cond
    (.contains keys-pressed \a)
    (.setMotorSpeed joint speed)
    (.contains keys-pressed \d)
    (.setMotorSpeed joint (- speed))
    :else (.setMotorSpeed joint 0))
  (notify keys-pressed)
  state)

(defn go [notify]
  (runner/run (init) (partial on-tick notify)))
