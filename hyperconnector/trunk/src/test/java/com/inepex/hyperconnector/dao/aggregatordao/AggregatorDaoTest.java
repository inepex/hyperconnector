package com.inepex.hyperconnector.dao.aggregatordao;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.Key;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.inepex.hyperconnector.dao.HyperOperationException;
import com.inepex.hyperconnector.dao.bottom.BottomLevelDao;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class AggregatorDaoTest {

	@Test
	public void simpleBehave() throws HyperOperationException {
		BottomLevelDao bottomMock = mock(BottomLevelDao.class);
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertEquals(6, ((Collection<?>)invocation.getArguments()[0]).size());
				return null;
			}
		}).when(bottomMock).insert(anyListOf(Cell.class));
		
		AggregatorDao dao = AggregatorDao.createTestDao(bottomMock);
		
		dao.insert(threeCells());
		dao.insert(threeCells());
		
		verifyZeroInteractions(bottomMock);
		
		dao.flushInserts();
		verify(bottomMock, times(1)).insert(anyListOf(Cell.class));
		verifyNoMoreInteractions(bottomMock);
		
		dao.flushInserts();
		dao.flushInserts();
		verifyNoMoreInteractions(bottomMock);
	}

	private List<Cell> threeCells() {
		return Arrays.asList(cell(), cell(), cell());
	}

	private Cell cell() {
		return new Cell(new Key("a", "b", "C", null)).setValue(new byte[]{(byte)1,(byte)2,(byte)3});
	}
	
}
