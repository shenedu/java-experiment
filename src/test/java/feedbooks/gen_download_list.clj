;generate download list -> meta.txt, feeded to DownloadManager.java

(ns feedbooks.gen-download-list
  (:use [clojure.contrib.pprint :only [pprint]])
  (:require [clojure.contrib.lazy-xml :as xml])
  (:import [java.io File FileWriter]
    [java.net URL]
    [feedbooks DownloadManager]))
  

(def seedroot (File. "/tmp/books/"))

(def allfiles (filter #(re-find #"atom" (str %)) (file-seq seedroot)))

(defn get-links [xml]
  (filter #(= (:tag %) :link) (:content xml)))

(defn get-id [file]
  (last (re-find #"books/(\d+)" (str file))))

(defn cover-uri [id]
  (str "http://covers.feedbooks.net/book/" id ".jpg?t=1301658884"))

(defn thumb-uri [id]
  (str "http://covers.feedbooks.net/book/" id ".jpg?size=thumbnail&t=1301658884"))

(defn categories-uri [id]
  (str "http://www.feedbooks.com/book/" id "/categories.atom"))

(defn comments-uri [id]
  (str "http://www.feedbooks.com/book/" id "/comments.atom"))

(defn similar-uri [id]
  (str "http://www.feedbooks.com/book/" id "/similar.atom"))

(defn download-links [links]
  (map #(-> % :attrs :href)
    (filter
      #(= "http://opds-spec.org/acquisition" (-> % :attrs :rel)) links)))

(defn get-name [uri]
  (re-find #"\d+\..+$" uri)) 

(defn download []
  (binding [*out* (FileWriter. "/tmp/meta.txt")]
    (doseq [file allfiles]
      (let [xml-tree (xml/parse-trim file)
            links (get-links xml-tree)
            file-links (download-links links)
            id (get-id file)]
        (println (str id ".jpg\t") (cover-uri id))
        (println (str id "-thumb.jpg\t") (thumb-uri id))
        (println (str id "-categories.atom\t") (categories-uri id))
        (println (str id "-comments.atom\t") (comments-uri id))
        (println (str id "-similar.atom\t") (similar-uri id))
        (doseq [link file-links]
          (println (get-name link) "\t" link))))))
