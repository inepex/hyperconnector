package com.inepex.hyperconnector.dao.aggregatordao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.Key;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.inepex.hyperconnector.ApplicationDelegate;
import com.inepex.hyperconnector.dao.HyperOperationException;
import com.inepex.hyperconnector.dao.bottom.BottomLevelDao;

public class AggregatorDaoTest {
	
	@Test
	public void poolTest() throws InterruptedException {
		assertNotNull(AggregatorDaoThreadPool.getExecutorService());
		Runnable mock = mock(Runnable.class);
		AggregatorDaoThreadPool.getExecutorService().scheduleAtFixedRate(mock, 5, 5, TimeUnit.MILLISECONDS);
		
		Thread.sleep(31);
		verify(mock, atLeast(6)).run();
	}

	@Test
	public void runTest() throws Exception {
		BottomLevelDao bottomMock = mock(BottomLevelDao.class);
		final AtomicInteger result = new AtomicInteger(0);
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				result.addAndGet(((Collection<?>)invocation.getArguments()[0]).size());
				return null;
			}
		}).when(bottomMock).insert(anyListOf(Cell.class), any(Runnable.class));
		
		AggregatorDao dao = new AggregatorDao(bottomMock, mock(ApplicationDelegate.class), false, 50);
		dao.insert(threeCells());
		Thread.sleep(10);
		dao.insert(threeCells());
		dao.insert(threeCells());
		Thread.sleep(60);
		dao.insert(threeCells());
		Thread.sleep(70);
		
		
		verify(bottomMock, atLeastOnce()).insert(anyListOf(Cell.class), any(Runnable.class));
		verifyNoMoreInteractions(bottomMock);
		assertEquals(12, result.get());
	}
	
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
		
		AggregatorDao dao = new AggregatorDao(bottomMock, null, true, 500);
		
		dao.insert(threeCells());
		dao.insert(threeCells());
		
		verifyZeroInteractions(bottomMock);
		
		dao.flushInserts();
		verify(bottomMock, times(1)).insert(anyListOf(Cell.class), any(Runnable.class));
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
