(ns gui-diff.parser
  (:require [net.cgrand.sjacket.parser :as parser]))


(defn- content-as-str [x]
  (cond (string? x)
        x

        (sequential? x)
        (apply str (map content-as-str x))

        (map? x)
        (content-as-str (:content x))

        :else
        (throw (Exception. (str (class x))))))

(defn- safe-read-string [s]
  (try
    (read-string s)
    (catch Exception _
      s)))

(defn parse-= [s]
  (let [nodes (:content (first (:content (parser/parser s))))]
    (->> nodes
         (remove #(or (string? %) (= :whitespace (:tag %))))
         (map content-as-str)
         (map safe-read-string))))
