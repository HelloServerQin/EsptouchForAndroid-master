package com.espressif.iot.esptouch.task;

public class EsptouchTaskParameter implements IEsptouchTaskParameter {

	private long mIntervalGuideCodeMillisecond;
	private long mIntervalDataCodeMillisecond;
	private long mTimeoutGuideCodeMillisecond;
	private long mTimeoutDataCodeMillisecond;
	private int mTotalRepeatTime;
	private int mEsptouchResultOneLen;
	private int mEsptouchResultMacLen;
	private int mEsptouchResultIpLen;
	private int mEsptouchResultTotalLen;
	private int mPortListening;
	private int mTargetPort;
	private int mWaitUdpReceivingMilliseond;
	private int mWaitUdpSendingMillisecond;
	private int mThresholdSucBroadcastCount;
	private int mExpectTaskResultCount;
	private static int _datagramCount = 0;

	public EsptouchTaskParameter() {
		mIntervalGuideCodeMillisecond = 8;//引导指南使用的时间毫秒
		mIntervalDataCodeMillisecond = 8; //数据
		mTimeoutGuideCodeMillisecond = 2000;//指南超时
		mTimeoutDataCodeMillisecond = 4000;//数据超时
		mTotalRepeatTime = 1;  //总重复的时间
		mEsptouchResultOneLen = 1;//one的长度
		mEsptouchResultMacLen = 6;//Mac 长度
		mEsptouchResultIpLen = 4;//ip长度
		mEsptouchResultTotalLen = 1 + 6 + 4;//结果总长度
		mPortListening = 18266;//端口
		mTargetPort = 7001;//目标端口
		mWaitUdpReceivingMilliseond = 15000;//接收的时间
		mWaitUdpSendingMillisecond = 45000;//发送
		mThresholdSucBroadcastCount = 1;//入口次数
		mExpectTaskResultCount = 1;//期望任务总数
	}

	// the range of the result should be 1-100
	private static int __getNextDatagramCount() {
		return 1 + (_datagramCount++) % 100;
	}

	@Override
	public long getIntervalGuideCodeMillisecond() {
		return mIntervalGuideCodeMillisecond;
	}

	@Override
	public long getIntervalDataCodeMillisecond() {
		return mIntervalDataCodeMillisecond;
	}

	@Override
	public long getTimeoutGuideCodeMillisecond() {
		return mTimeoutGuideCodeMillisecond;
	}

	@Override
	public long getTimeoutDataCodeMillisecond() {
		return mTimeoutDataCodeMillisecond;
	}

	@Override
	public long getTimeoutTotalCodeMillisecond() {
		return mTimeoutGuideCodeMillisecond + mTimeoutDataCodeMillisecond;
	}

	@Override
	public int getTotalRepeatTime() {
		return mTotalRepeatTime;
	}

	@Override
	public int getEsptouchResultOneLen() {
		return mEsptouchResultOneLen;
	}

	@Override
	public int getEsptouchResultMacLen() {
		return mEsptouchResultMacLen;
	}

	@Override
	public int getEsptouchResultIpLen() {
		return mEsptouchResultIpLen;
	}

	@Override
	public int getEsptouchResultTotalLen() {
		return mEsptouchResultTotalLen;
	}

	@Override
	public int getPortListening() {
		return mPortListening;
	}

	// target hostname is : 234.1.1.1, 234.2.2.2, 234.3.3.3 to 234.100.100.100
	@Override
	public String getTargetHostname() {
		int count = __getNextDatagramCount();
		return "234." + count + "." + count + "." + count;
	}

	@Override
	public int getTargetPort() {
		return mTargetPort;
	}

	@Override
	public int getWaitUdpReceivingMillisecond() {
		return mWaitUdpReceivingMilliseond;
	}

	@Override
	public int getWaitUdpSendingMillisecond() {
		return mWaitUdpSendingMillisecond;
	}

	@Override
	public int getWaitUdpTotalMillisecond() {
		return mWaitUdpReceivingMilliseond + mWaitUdpSendingMillisecond;
	}

	@Override
	public int getThresholdSucBroadcastCount() {
		return mThresholdSucBroadcastCount;
	}

	@Override
	public void setWaitUdpTotalMillisecond(int waitUdpTotalMillisecond) {
		if (waitUdpTotalMillisecond < mWaitUdpReceivingMilliseond
				+ getTimeoutTotalCodeMillisecond()) {
			// if it happen, even one turn about sending udp broadcast can't be
			// completed
			throw new IllegalArgumentException(
					"waitUdpTotalMillisecod is invalid, "
							+ "it is less than mWaitUdpReceivingMilliseond + getTimeoutTotalCodeMillisecond()");
		}
		mWaitUdpSendingMillisecond = waitUdpTotalMillisecond
				- mWaitUdpReceivingMilliseond;
	}

	@Override
	public int getExpectTaskResultCount() {
		return this.mExpectTaskResultCount;
	}

	@Override
	public void setExpectTaskResultCount(int expectTaskResultCount) {
		this.mExpectTaskResultCount = expectTaskResultCount;
	}

}
