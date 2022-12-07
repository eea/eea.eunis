import requests
import mysql.connector as conn
import copy
import csv
import pandas as pd

db = conn.connect(host="localhost",  # your host
                     user="eunisuser",  # your username
                     passwd="",  # your password
                     db="eunis",
                     port=3306,
                     use_unicode=True,
                     charset="utf8",
                     collation="utf8_general_ci")  # name of the data base

cur = db.cursor()

data = pd.read_csv (r'./eunis_habitats_map.csv')   
df = pd.DataFrame(data)

#cur.execute(" CREATE TABLE chm62edt_habitat_maps (habitat_code varchar(16) primary key, habitat_name text, distribution_map int, suitability_map int, probability_map int)")
#db.commit() 

for row in df.itertuples():
    cur.execute("INSERT INTO chm62edt_habitat_maps (habitat_code, habitat_name, distribution_map, suitability_map, probability_map) VALUES (%s,%s,%s,%s,%s)",
        (row[1],
        row[2], 
        row[3] == 'x',
        row[4] == 'x',
        row[5] == 'x')
    )

db.commit()