CREATE DATABASE `BrainNet` /*!40100 DEFAULT CHARACTER SET latin1 */;

Use BrainNet;

CREATE TABLE `AdminInfo` (
  `AdminID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  `Gender` varchar(10) DEFAULT NULL,
  `Age` int(11) DEFAULT NULL,
  PRIMARY KEY (`AdminID`)
) ENGINE=InnoDB AUTO_INCREMENT=12345679 DEFAULT CHARSET=latin1;


CREATE TABLE `UBrainData` (
  `ID` int(11) NOT NULL,
  `timestamp` timestamp NULL DEFAULT NULL,
  `SessionID` varchar(100) NOT NULL,
  `data` varchar(1000) DEFAULT NULL,
  KEY `id_index` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `UserInfo` (
  `UserID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  `Gender` varchar(10) DEFAULT NULL,
  `Age` int(11) DEFAULT NULL,
  PRIMARY KEY (`UserID`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=latin1;

