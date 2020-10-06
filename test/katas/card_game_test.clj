(ns katas.card-game-test
  (:require [midje.sweet :refer :all]
            [katas.card-game :refer :all]))

(fact "add Mana"
      (increase-mana {:mana-pool 0}) => (contains {:mana-pool 1})                    ;#(= (:mana-pool %) 1)
      (increase-mana {:mana-pool 1}) => (contains {:mana-pool 2})
      (increase-mana {:mana-pool 10}) => (contains {:mana-pool 10})
      (increase-mana {:mana-pool 9}) => (contains {:mana-pool 10})
      (next-turn init) => (contains {:player1 (contains {:mana-pool 1})})
      (next-turn (next-turn (next-turn init))) => (and (contains {:player2 (contains {:mana-pool 1})}) (contains {:player1 (contains {:mana-pool 2})})))

(fact
  (increase-mana {:mana 0 :mana-pool 0}) => (contains {:mana 1})
  (increase-mana {:mana 0 :mana-pool 1}) => (contains {:mana 2})
  (increase-mana {:mana 0 :mana-pool 10}) => (contains {:mana 10}))

;(fact "initialize player"
;      init => {:player1        {:mana 0 :health 30}
;               :player2        {:mana 0 :health 30}
;               :current-player :player2})

(fact "player changes"
      (next-turn init) => (contains {:current-player :player1})
      (next-turn (next-turn init)) => (contains {:current-player :player2}))


(fact "lose health"
      (let [start-health 5]
        (lose-health {:health start-health} 0) => {:health 5}
        (lose-health {:health start-health} 1) => {:health 4}
        (lose-health {:health start-health} 3) => {:health 2}
        (lose-health (:player1 init) 3) => (contains {:health 27})))

(defn stub-rand
  ([value call]
   (with-redefs-fn
     {#'clojure.core/rand-int (fn [max] value)}
     call)))

(facts "draw cards"
       (fact "one size deck"
             (draw-card {:hand [] :deck [0]}) => {:hand [0] :deck []}
             (draw-card {:hand [] :deck [1]}) => {:hand [1] :deck []}
             (draw-card {:hand [0] :deck [1]}) => {:hand [0 1] :deck []})
       (fact "bigger size deck"
             (stub-rand 0
                        #(draw-card {:hand [0] :deck [1 2]}))
             => {:hand [0 1] :deck [2]}
             (stub-rand 1
                        #(draw-card {:hand [0 1] :deck [2 3]}))
             => {:hand [0 1 3] :deck [2]}
             (stub-rand 18
               #(draw-card (:player1 init)))
             =not=> (contains {:deck (contains 7)})
             (stub-rand 18
                        #(draw-card (:player1 init)))
             => (contains {:hand (just 7)}))
       (fact "no hand cards draw at turn start"
             (next-turn (next-turn init)) => #(and (and
                                                     (= 1 (count (:hand (:player1 %))))
                                                     (= 19 (count (:deck (:player1 %)))))
                                                   (and
                                                     (= 1 (count (:hand (:player2 %))))
                                                     (= 19 (count (:deck (:player2 %))))))))

(fact "pay mana"
  (pay-mana {:mana 0} 0) => (contains {:mana 0})
  (pay-mana {:mana 1} 0) => (contains {:mana 1})
  (pay-mana {:mana 1} 1) => (contains {:mana 0})
  (pay-mana {:mana 9} 5) => (contains {:mana 4}))

(fact "remove hand card"
  (remove-card {:hand [0]} 0) => (contains {:hand []})
  (remove-card {:hand [0 1]} 0) => (contains {:hand [1]})
  (remove-card {:hand [0 1 2]} 1) => (contains {:hand [0 2]}))

(fact
  (Play-card
    {:player1 {:hand [0] :mana 0}
     :player2 {:health 10}
     :current-player :player1} 0)
  => (contains
       {:player1 (contains {:hand [] :mana 0})
        :player2 (contains {:health 10})})
  (play-card
    {:player2 {:hand [0] :mana 0}
     :player1 {:health 10}
     :current-player :player2} 0)
  => (contains
       {:player2 (contains {:hand [] :mana 0})
        :player1 (contains {:health 10})})
  (play-card
    {:player1 {:hand [0 1] :mana 5}
     :player2 {:health 10}
     :current-player :player1} 1)
  => (contains
       {:player1 (contains {:hand [0] :mana 4})
        :player2 (contains {:health 9})}))

(fact
  )

(fact
  (-main) => -1)