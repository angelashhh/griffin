(ns bank-account.journey-entry
    (:require [clojure.spec.alpha :as s]
              [clojure.set :refer :all]))

(s/def ::id uuid?)
(s/def ::account-id uuid?)
(s/def ::type #{:credit :debit})
(s/def ::amount pos-int?)
(s/def ::line-item (s/keys :req-un [::account-id ::amount ::type]))
(s/def ::line-items (s/coll-of ::line-item :min-count 2))
(s/def ::journey-entry (s/keys ::req-un [::id ::line-items]))

(defn get-items-per-type [line-items type]
    {:pre [(s/valid? ::line-items line-items) (s/valid? ::type type)]}
    (let [is-type? #(= type (:type %))]
        (filter is-type? line-items)))

(defn get-total-amount [line-items]
    {:pre [(s/valid? ::line-items line-items)]}
    (let [sum-up-amount #(+ (:amount %1) (:amount %2))]
        (reduce sum-up-amount line-items)))

(defn is-balanced? [journey-entry]
    {:pre [(s/valid? ::journey-entry journey-entry)]}
    (let [all-items (:line-items journey-entry)
          credit-items (get-items-per-type all-items :credit)
          debit-items (get-items-per-type all-items :debit)]
        (= (get-total-amount credit-items) (get-total-amount debit-items))))
