# Data Anonymisation

Anonymisation is removing personally identifiable information (PII) from data to protect the privacy of individuals. The main goals of anonymisation are to preserve data utility, privacy, and data truthfulness. Data utility refers to the amount of loss caused by the anonymisation technique, such as information loss. The conformity of the data to privacy model constraints measures privacy. Data truthfulness ensures that each anonymized record corresponds to a single record in the original table.

## Anonymisation Models

Various models and techniques are used for anonymisation, each with advantages and disadvantages. Some of the most common models are:

- **K-anonymity**: This model requires that at least k individuals share the exact attributes. This guarantees that the attacker cannot identify the identity of a single record. However, a high value of k can lead to information loss and limit the privacy offered by the model.
- **L-diversity**: This model overcomes the limitations of k-anonymity by considering diversity among sensitive attributes. L-diversity ensures at least l distinct values of sensitive points in each equivalence class.
- **T-closeness**: This model ensures that the distance between the distribution of sensitive values in each equivalence class and the original style is no more than a threshold t. A smaller value of t represents more robust privacy.

## Anonymisation Techniques

Anonymisation techniques include:

- **Pseudonymisation**: This technique replaces data in one or more columns to preserve its appearance while ensuring privacy. However, this technique may affect the value of the data, limiting the control over privacy and utility.
- **Generalisation**: This technique involves omitting specific data to make it less recognizable or organizing data into categories instead of precise quantities. This strikes a balance between data value and privacy. However, generalisation can result in significant information loss, and the data may be altered.
- **Differential Privacy**: This technique inserts noise into the data while maintaining its statistical distribution. It is suitable for numerical data and balances usability and privacy by selecting the optimal control settings.
- **Synthetic Data**: This technique involves creating new artificial data that resembles a real dataset while maintaining a balance between privacy and usefulness. This technique is applicable to all data formats, but it requires the expertise of a data engineer to execute.
- **Slicing**: This technique separates the data into horizontal and vertical groups to ensure that strongly connected characteristics are grouped.
- **Suppression and Relocation**: This technique is used to prevent overgeneralisation. Suppression involves eliminating outliers, while relocation involves modifying their values. Suppression can be done at the record or cell level and may reduce information loss.
- **Perturbation**: This technique replaces sensitive values and QIDs with fictitious masks of the original data. This technique improves data usefulness since the degree of generalisation is constrained.
- **Bucketisation**: This technique distinguishes sensitive characteristics from QIDs by randomly permuting or exchanging the values of sensitive attributes inside each bucket. This technique is more beneficial than generalisation but does not prevent membership disclosure.
- **Microaggregation**: This technique creates k-anonymous equivalent classes by masking critical features. Microaggregation replaces the values of the k closest groupings of records with their centroid, but finding the optimum solution is known to be NP-hard.

# Conclusion

In conclusion, anonymisation is a complex process that requires careful consideration of privacy, data utility, and data truthfulness. The choice of anonymisation model and technique depends on the specific requirements of the data and the desired trade-off between privacy and data value.
