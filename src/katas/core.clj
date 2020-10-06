(ns katas.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(def full-deck [0 0 1 1 2 2 2 3 3 3 3 4 4 4 5 5 6 6 7 8])

(def player-1 (atom {:mana-pool 0
                     :mana 0
                     :health    30
                     :deck      full-deck
                     :hand      (empty full-deck)}))

(def player-2 (atom {}))

(defn start-game []
  )

(defn player-gets-mana [player]
  (when (> 10 (:mana-pool @player))
    (swap! player update-in [:mana-pool] inc)
    (swap! player assoc :mana-pool (:mana-pool @player))))

(defn remove-index [collection index]
  (vec (concat (subvec collection 0 index)
               (subvec collection (inc index)))))

(defn remove-drawn-card [player index]
  (swap! player assoc :deck (remove-index (:deck @player) index)))

(defn draw-card [player]
  (let [drawn-card (rand-nth (:deck @player))
        index (.indexOf (:deck @player) drawn-card)]
    (do
      (remove-drawn-card player index)
      (swap! player update-in [:hand] conj drawn-card))))

(defn pay-mana [player amount]
  (let [new (- (:mana-pool @player) amount)]
    (if (pos? new)
      (swap! player assoc :mana-pool new))))

(defn damage-player [player amount]
  (let [new-life (- (:health @player) amount)]
    (if (< 0 new-life)
      (swap! player assoc :health new-life)
      ())))

(defn remove-hand-card [player index]
  (swap! player assoc :hand (remove-index (:hand @player) index)))

(defn play-card [player index]
  (when-let [card (get-in (:hand @player) index)]
    (when (pay-mana player card)
      (damage-player player card)
      (remove-hand-card player index))))