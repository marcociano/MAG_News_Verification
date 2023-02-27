# -*- coding: utf-8 -*-

import DataPrep
import FeatureSelection
import numpy as np
import pickle
from sklearn.pipeline import Pipeline
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import KFold
from sklearn.metrics import confusion_matrix, f1_score, classification_report
from sklearn.model_selection import learning_curve
import matplotlib.pyplot as plt
from sklearn.metrics import precision_recall_curve
from sklearn.metrics import average_precision_score

# string to test
doc_new = ['obama is running for president in 2016']


# User defined functon for K-Fold cross validatoin
def build_confusion_matrix(classifier):
    k_fold = KFold(n_splits=5)
    scores = []
    confusion = np.array([[0, 0], [0, 0]])

    for train_ind, test_ind in k_fold.split(DataPrep.train_news):
        train_text = DataPrep.train_news.iloc[train_ind]['Statement']
        train_y = DataPrep.train_news.iloc[train_ind]['Label']

        test_text = DataPrep.train_news.iloc[test_ind]['Statement']
        test_y = DataPrep.train_news.iloc[test_ind]['Label']

        classifier.fit(train_text, train_y)
        predictions = classifier.predict(test_text)

        confusion += confusion_matrix(test_y, predictions)
        score = f1_score(test_y, predictions)
        scores.append(score)

    return (print('Total statements classified:', len(DataPrep.train_news)),
            print('Score:', sum(scores) / len(scores)),
            print('score length', len(scores)),
            print('Confusion matrix:'),
            print(confusion))


# logistic regression classifier
logR_pipeline_ngram = Pipeline([
    ('LogR_tfidf', FeatureSelection.tfidf_ngram),
    ('LogR_clf', LogisticRegression(penalty="l2", C=1))
])

logR_pipeline_ngram.fit(DataPrep.train_news['Statement'], DataPrep.train_news['Label'])
predicted_LogR_ngram = logR_pipeline_ngram.predict(DataPrep.test_news['Statement'])
np.mean(predicted_LogR_ngram == DataPrep.test_news['Label'])


build_confusion_matrix(logR_pipeline_ngram)

print(classification_report(DataPrep.test_news['Label'], predicted_LogR_ngram))


DataPrep.test_news['Label'].shape

# saving best model to the disk
model_file = 'final_model.sav'
pickle.dump(logR_pipeline_ngram, open(model_file, 'wb'))


# Plotting learing curve
def plot_learing_curve(pipeline, title):
    size = 10000
    cv = KFold(size, shuffle=True)

    X = DataPrep.train_news["Statement"]
    y = DataPrep.train_news["Label"]

    pl = pipeline
    pl.fit(X, y)

    train_sizes, train_scores, test_scores = learning_curve(pl, X, y, n_jobs=-1, cv=cv,
                                                            train_sizes=np.linspace(.1, 1.0, 5), verbose=0)

    train_scores_mean = np.mean(train_scores, axis=1)
    train_scores_std = np.std(train_scores, axis=1)
    test_scores_mean = np.mean(test_scores, axis=1)
    test_scores_std = np.std(test_scores, axis=1)

    plt.figure()
    plt.title(title)
    plt.legend(loc="best")
    plt.xlabel("Training examples")
    plt.ylabel("Score")
    plt.gca().invert_yaxis()

    # box-like grid
    plt.grid()

    # plot the std deviation as a transparent range at each training set size
    plt.fill_between(train_sizes, train_scores_mean - train_scores_std, train_scores_mean + train_scores_std, alpha=0.1,
                     color="r")
    plt.fill_between(train_sizes, test_scores_mean - test_scores_std, test_scores_mean + test_scores_std, alpha=0.1,
                     color="g")

    # plot the average training and test score lines at each training set size
    plt.plot(train_sizes, train_scores_mean, 'o-', color="r", label="Training score")
    plt.plot(train_sizes, test_scores_mean, 'o-', color="g", label="Cross-validation score")

    # sizes the window for readability and displays the plot
    # shows error from 0 to 1.1
    plt.ylim(-.1, 1.1)
    plt.show()


# below command will plot learing curves for each of the classifiers
plot_learing_curve(logR_pipeline_ngram, "LogisticRegression Classifier")


"""
by plotting the learning cureve for logistic regression, it can be seen that cross-validation score is stagnating throughout and it 
is unable to learn from data. Also we see that there are high errors that indicates model is simple and we may want to increase the
model complexity.
"""


# plotting Precision-Recall curve
def plot_PR_curve(classifier):
    precision, recall, thresholds = precision_recall_curve(DataPrep.test_news['Label'], classifier)
    average_precision = average_precision_score(DataPrep.test_news['Label'], classifier)

    plt.step(recall, precision, color='b', alpha=0.2,
             where='post')
    plt.fill_between(recall, precision, step='post', alpha=0.2,
                     color='b')

    plt.xlabel('Recall')
    plt.ylabel('Precision')
    plt.ylim([0.0, 1.05])
    plt.xlim([0.0, 1.0])
    plt.title('2-class Random Forest Precision-Recall curve: AP={0:0.2f}'.format(
        average_precision))


plot_PR_curve(predicted_LogR_ngram)
# plot_PR_curve(predicted_rf_ngram)


"""
Now let's extract the most informative feature from ifidf vectorizer for all fo the classifiers and see of there are any common
words that we can identify i.e. are these most informative feature acorss the classifiers are same? we will create a function that 
will extract top 50 features.
"""


def show_most_informative_features(model, vect, clf, text=None, n=50):
    # Extract the vectorizer and the classifier from the pipeline
    vectorizer = model.named_steps[vect]
    classifier = model.named_steps[clf]

    # Check to make sure that we can perform this computation
    if not hasattr(classifier, 'coef_'):
        raise TypeError(
            "Cannot compute most informative features on {}.".format(
                classifier.__class__.__name__
            )
        )

    if text is not None:
        # Compute the coefficients for the text
        tvec = model.transform([text]).toarray()
    else:
        # Otherwise simply use the coefficients
        tvec = classifier.coef_

    # Zip the feature names with the coefs and sort
    coefs = sorted(
        zip(tvec[0], vectorizer.get_feature_names()),
        reverse=True
    )

    # Get the top n and bottom n coef, name pairs
    topn = zip(coefs[:n], coefs[:-(n + 1):-1])

    # Create the output string to return
    output = []

    # If text, add the predicted value to the output.
    if text is not None:
        output.append("\"{}\"".format(text))
        output.append(
            "Classified as: {}".format(model.predict([text]))
        )
        output.append("")

    # Create two columns with most negative and most positive features.
    for (cp, fnp), (cn, fnn) in topn:
        output.append(
            "{:0.4f}{: >15}    {:0.4f}{: >15}".format(
                cp, fnp, cn, fnn
            )
        )
    print(output)


show_most_informative_features(logR_pipeline_ngram, vect='LogR_tfidf', clf='LogR_clf')

