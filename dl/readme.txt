So in data layer if any problem occurs then we have to raise exception, for this we are making DAOException class and this class will extend Exception class so it will have checked exceptions

we will also make DAO and DTO interfaces for DAO and DTO classes. DTO interface should be comparable and serializable. And then we will be writing classes for DTO and DAO 