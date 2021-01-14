(ns bank-account.journney-entry-test
  (:require [clojure.test :refer :all]
            [bank-account.journey-entry :as j]
            [clojure.spec.alpha :as s])
  (:import (java.util UUID)))

(defn create-line [amount type]
  {:account-id (UUID/randomUUID) :amount amount :type type})

(deftest bank-account-test
  (let [
        credit-line-1 (create-line 111 :credit)
        credit-line-2 (create-line 222 :credit)
        debit-line-1 (create-line 111 :debit)
        debit-line-2 (create-line 222 :debit)

        line-items (list credit-line-1 debit-line-1 credit-line-2 debit-line-2)
        journey-entry {:id (UUID/randomUUID) :line-items line-items}]

    (testing "can get all credit lines"
      (let [calculated-credit-lines (j/get-items-per-type line-items :credit)]
        (is (= (list credit-line-1 credit-line-2) calculated-credit-lines))))

    (testing "can get a sum of all credit lines"
      (let [credit-lines (list credit-line-1 credit-line-2)
            calculated-credit-sum (j/get-total-amount credit-lines)]
        (is (= 333 calculated-credit-sum))))

    (testing "successfully show balanced accounts"
      (is (true? (j/is-balanced? journey-entry))))

    (testing "successfully show unbalanced accounts"
      (let [false-debit-line (create-line 333 :debit)
            false-line-items (list credit-line-1 debit-line-1 credit-line-2 false-debit-line)
            unbalanced-journey-entry {:id (UUID/randomUUID) :line-items false-line-items}]
        (is (false? (j/is-balanced? unbalanced-journey-entry)))))
  ))
