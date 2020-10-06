(ns katas.card-game)

(defn remove-index [collection index]
  (vec (concat (subvec collection 0 index)
               (subvec collection (inc index)))))

(def player {:mana-pool 0 :health 30
             :deck      [0 0 1 1 2 2 2 3 3 3 3 4 4 4 5 5 6 6 7 8]
             :hand      []})

(def init
  {:player1        player
   :player2        player
   :current-player :player2})

(defn increase-mana [player]
  (let [current (:mana-pool player)
        refill (partial assoc player :mana)]
    (if (> 10 current)
      (let [new (inc current)]
        (assoc (refill new) :mana-pool new))
      (refill current))))

(defn lose-health [player amount]
  (assoc player :health (- (:health player) amount)))

(defn draw-card [player]
  (let [index (rand-int (count (:deck player)))]
    (update-in
      (update-in
        player
        [:hand] #(conj % (get (:deck player) index)))
      [:deck] #(remove-index % index))))

(defn pay-mana [player to-pay]
  (update-in player [:mana] #(- % to-pay)))

(defn remove-card [player index]
  (update-in player [:hand] #(remove-index % index)))

(defn next-player [state]
  (assoc state :current-player
               (if (= (:current-player state) :player1)
                 :player2
                 :player1)))

(defn play-card [state index]
  (let [current (:current-player state)
        card (nth (:hand (current state)) index)
        enemy (:current-player (next-player state))]
    (assoc
      (assoc state enemy (lose-health (enemy state) card))
      current
      (remove-card
        (pay-mana (current state) card)
        index))))

(defn next-turn [state]
  (let [current (next-player state)
        player (:current-player current)]
    (assoc current player
                   (->
                     (draw-card (player current))
                     (increase-mana)))))

(defn -main [& args]
  -1)

