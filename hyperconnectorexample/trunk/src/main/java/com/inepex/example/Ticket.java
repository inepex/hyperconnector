package com.inepex.example;

import com.inepex.hyperconnector.annotations.HyperEntity;
import com.inepex.hyperconnector.annotations.RowKeyField;
import com.inepex.hyperconnector.annotations.SerializationTypes;
import com.inepex.hyperconnector.annotations.SortOrders;
import com.inepex.hyperconnector.annotations.Timestamp;
import com.inepex.hyperconnector.annotations.TimestampAssignmentTypes;
import com.inepex.hyperconnector.annotations.ValueField;

@HyperEntity(namespace="UserSupport", table="Ticket", rowKeySerializationType = SerializationTypes.PLAIN, globalValueSerializationType = SerializationTypes.PLAIN, globalColumnQualifierSerializationType=SerializationTypes.PLAIN)
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
        
}