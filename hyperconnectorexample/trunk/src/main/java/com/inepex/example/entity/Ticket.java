package com.inepex.example.entity;

import com.inepex.hyperconnector.annotations.HyperEntity;
import com.inepex.hyperconnector.annotations.RowKeyField;
import com.inepex.hyperconnector.annotations.SerializationTypes;
import com.inepex.hyperconnector.annotations.SortOrders;
import com.inepex.hyperconnector.annotations.Timestamp;
import com.inepex.hyperconnector.annotations.TimestampAssignmentTypes;
import com.inepex.hyperconnector.annotations.ValueField;

@HyperEntity(namespace="TestUserSupportNameSpace", table="Ticket", rowKeySerializationType = SerializationTypes.PLAIN, 
	globalValueSerializationType = SerializationTypes.PLAIN, globalColumnQualifierSerializationType=SerializationTypes.PLAIN)
public class Ticket {
        @RowKeyField(order=0, sortOrder=SortOrders.ASCENDING)
        private String server;
        
        @Timestamp(assignmentType=TimestampAssignmentTypes.MANUAL)
        private long timestamp;
        
        @ValueField(columnFamily="title")
        private String title;
        @ValueField(columnFamily="description")
        private String description;
        @ValueField(columnFamily="priority")
        private String priority;
        @ValueField(columnFamily="problemtype")
        private String problemType;
        @ValueField(columnFamily="isFixed")
        private boolean isFixed;
        
        
        public String getServer() {
                return server;
        }
        public void setServer(String server) {
                this.server = server;
        }
        public long getTimestamp() {
                return timestamp;
        }
        public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
        }
        public String getTitle() {
                return title;
        }
        public void setTitle(String title) {
                this.title = title;
        }
        public String getDescription() {
                return description;
        }
        public void setDescription(String description) {
                this.description = description;
        }
        public String getPriority() {
                return priority;
        }
        public void setPriority(String priority) {
                this.priority = priority;
        }
        public String getProblemType() {
                return problemType;
        }
        public void setProblemType(String problemType) {
                this.problemType = problemType;
        }
        public boolean getIsFixed() {
                return isFixed;
        }
        public void setIsFixed(boolean isFixed) {
                this.isFixed = isFixed;
        }
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((description == null) ? 0 : description.hashCode());
			result = prime * result + (isFixed ? 1231 : 1237);
			result = prime * result
					+ ((priority == null) ? 0 : priority.hashCode());
			result = prime * result
					+ ((problemType == null) ? 0 : problemType.hashCode());
			result = prime * result
					+ ((server == null) ? 0 : server.hashCode());
			result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
			result = prime * result + ((title == null) ? 0 : title.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Ticket other = (Ticket) obj;
			if (description == null) {
				if (other.description != null)
					return false;
			} else if (!description.equals(other.description))
				return false;
			if (isFixed != other.isFixed)
				return false;
			if (priority == null) {
				if (other.priority != null)
					return false;
			} else if (!priority.equals(other.priority))
				return false;
			if (problemType == null) {
				if (other.problemType != null)
					return false;
			} else if (!problemType.equals(other.problemType))
				return false;
			if (server == null) {
				if (other.server != null)
					return false;
			} else if (!server.equals(other.server))
				return false;
			if (timestamp != other.timestamp)
				return false;
			if (title == null) {
				if (other.title != null)
					return false;
			} else if (!title.equals(other.title))
				return false;
			return true;
		}
}