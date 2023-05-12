# Requirements

## Functional requirements:

1. Ability to handle various data types, including categorical and real-valued data.

   - Leveraged the ARX library's powerful features for data anonymization.
   - Used ARX's data generalization techniques for anonymizing categorical data.
   - Utilized ARX's micro-aggregation techniques for anonymizing real-valued data.
   - Successfully handled different data types effectively and efficiently.
   - Protected sensitive patient information while preserving its usability for analysis and research.

2. Implementation of multiple data anonymization techniques to effectively anonymize patient information while preserving the integrity and meaning of the data.

   - Utilized various algorithms such as:
     - K-anonymity: a method of ensuring that individuals in a dataset cannot be re-identified by combining quasi-identifiers (e.g. age, gender, zip code) with external information.
     - L-diversity: a technique that aims to prevent attribute disclosure by ensuring that sensitive attributes (e.g. disease status) have a minimum number of diverse values within each group of quasi-identifiers.
     - T-closeness: a method that measures the distance between the distribution of sensitive attributes in the original data and the distribution in the anonymized data to ensure that they are "close" enough to preserve privacy.
   - Implemented these algorithms to anonymize patient information.

3. Integration with existing Data Foundation systems for ease of use and a seamless user experience.

   - Used ReactJS and dockerised the application for ease of use and a seamless integration with the existing Data Foundation systems and to provide a user-friendly interface.

4. Ability to handle large data sets and accommodate future growth and demand.

   - Designed the system to be scalable to handle large data sets.

5. Compliance with privacy regulations and standards, such as GDPR.

   - Ensured compliance with privacy regulations and standards, as all data is anonymized and cannot be re-identified.

6. User-friendly interface for easy configuration and management of data anonymization settings.
   - Developed a user-friendly interface for easy configuration and management of data anonymization settings.

## Non-functional requirements:

- Performance: The solution must process data in a timely and efficient manner, with minimal impact on system performance.

  - Ensured that the solution processed data in a timely and efficient manner, with minimal impact on system performance.
  - A 100 MB parse took less than 10 seconds as per our analysis.

- Security: The solution must ensure the confidentiality of patient information and protect against unauthorized access or breaches.

  - Implemented robust security measures to protect patient information against unauthorized access or breaches.
  - Ensured that all data was anonymized and could not be re-identified.
  - Users have power over how much information they want to anonymize.

- Scalability: The solution must be designed to scale to accommodate future growth and demand.

  - Designed the solution to be scalable to accommodate future growth and demand.

- Reliability: The solution must be reliable, with minimal downtime or errors.

  - Ensured the solution was reliable, with minimal downtime or errors.
  - We ensured that no software bugs were present in the solution.

- Maintainability: The solution must be easily maintainable, with clear documentation and code structure.

  - Maintained clear documentation and code structure to make the solution easily maintainable.

- Usability: The solution must be user-friendly, and easy to use, with clear and concise instructions and guidance.
  - Designed the solution to be user-friendly, with clear and concise instructions and guidance.

## Flow Diagrams

Can be accessed at [Flow Diagrams](/intro#activity-diagrams)

## Use Cases

Can be accessed at [Use Cases](/intro#representation)

## Stakeholder buy-in

As we worked on the project, we recognized the importance of stakeholder buy-in, particularly from the doctors and professors who would be using and relying on our solution. To ensure that we were meeting their needs and preferences, we actively engaged with them throughout the development process. We held meetings and discussions to gather feedback, understand their pain points and requirements, and discuss potential solutions. By actively involving them in the development process, we were able to gain a better understanding of their perspective and make adjustments to the solution to meet their needs. This helped to build trust, collaboration, and engagement among stakeholders, and ensured that the final solution was effective and valuable to those who would be using it. Overall, this was crucial to the success of the project and our ability to deliver a solution that met the needs of our users.
