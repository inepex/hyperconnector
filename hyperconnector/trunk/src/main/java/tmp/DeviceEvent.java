package tmp;

import java.io.Serializable;

import com.inepex.hyperconnector.annotations.CellFields;
import com.inepex.hyperconnector.annotations.HyperEntity;
import com.inepex.hyperconnector.annotations.RowKeyField;
import com.inepex.hyperconnector.annotations.SerializationTypes;
import com.inepex.hyperconnector.annotations.SortOrders;
import com.inepex.hyperconnector.annotations.Timestamp;
import com.inepex.hyperconnector.annotations.TimestampAssignmentTypes;
import com.inepex.hyperconnector.annotations.ValueField;

@SuppressWarnings("serial")
@HyperEntity(namespace="InepexLbs", table="DeviceEvent",
	rowKeySerializationType=SerializationTypes.FIELDPROCESSOR,
	dumped=true)
	public class DeviceEvent implements Serializable {
	@RowKeyField(order=0)
	private long deviceId;
	@RowKeyField(order=1, sortOrder=SortOrders.DESCENDING)
	private long creationTimestamp;

	@Timestamp(assignmentType=TimestampAssignmentTypes.AUTO_HYPERCONNECTOR)
	private long storageTimestamp;
	
	@ValueField(columnFamily="d", cellField = CellFields.COLUMNQUALIFIER,order=0)
	private String name;

	@ValueField(columnFamily="d", cellField = CellFields.VALUE, order=0)
	private String message;

	@ValueField(columnFamily="d", cellField = CellFields.VALUE, order=1)
	private Long confirmDate;
	
	@ValueField(columnFamily="d", cellField = CellFields.VALUE, order=2)
	private Long confirmUser;
	
	public DeviceEvent() {
	}

	
	public long getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	public long getCreationTimestamp() {
		return creationTimestamp;
	}
	public void setCreationTimestamp(long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	public long getStorageTimestamp() {
		return storageTimestamp;
	}
	public void setStorageTimestamp(long storageTimestamp) {
		this.storageTimestamp = storageTimestamp;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getConfirmDate() {
		return confirmDate;
	}
	public void setConfirmDate(Long confirmDate) {
		this.confirmDate = confirmDate;
	}
	public Long getConfirmUser() {
		return confirmUser;
	}
	public void setConfirmUser(Long confirmUser) {
		this.confirmUser = confirmUser;
	}

	@Override
	public String toString() {
		return "DeviceEvent [deviceId:" + deviceId + ", creationTimestamp=" + creationTimestamp + ", name=" + name + "]";
	}
	
	
}
