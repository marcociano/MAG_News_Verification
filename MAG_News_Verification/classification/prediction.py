# -- coding: utf-8 --
import pickle
from flask import Flask
import os

# doc_new = ['obama is running for president in 2016']

app = Flask(__name__)


@app.route('/')
def hello_world():
    return 'Hello World!'


dochtml = os.path.abspath("/Users/marcociano/git/MAG_News_Verification/MAG_News_Verification/docJsoup.html")


# function to run for prediction
@app.route('/detecting', methods=['GET'])
def detecting_fake_news():
    filehtml = open(dochtml, "r")
    content = filehtml.read()
    # retrieving the best model for prediction call
    load_model = pickle.load(open('final_model.sav', 'rb'))
    prediction = load_model.predict([content])
    prob = load_model.predict_proba([content])
    return str(prediction[0]) + ' ' + str(prob[0][1])


if __name__ == '__main__':
    app.run(debug=True, host='127.0.0.1')
    detecting_fake_news()
