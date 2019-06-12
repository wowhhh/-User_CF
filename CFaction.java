package com.wyb.rec.CollaborationFiltering;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.catalina.authenticator.SavedRequest;
import org.junit.Test;

import sun.security.x509.KeyIdentifier;

/**
 * 用于控制整个算法的过程
 * @author wyb
 *
 */
public class CFaction {
	
	@Test
	public void test()
	{
		this.run();
		//this.run_songListRec();
	}
	
	public Map<Integer,Integer[]> run()
	{
	//开始
	//用户-歌曲 推荐列表
	Map<Integer,Integer[]> user2songRecMatrix=new HashMap<Integer, Integer[]>();
	
	//获取用户
	List<Integer> userIdList=DataTranslate.GetuserIdList();
	//获取歌曲
	List<Integer> songIdList=DataTranslate.GetsongIdList();
	//获取用户评分矩阵
	Map<Integer, double[]> user2songRatingMatrix=DataTranslate.GetUserItemMatrix(userIdList, songIdList);
	//计算用户相似度，获取用户k个近邻用户
	Map<Integer,Integer[]> userKNNMatrix=UserKNN.getKNN(userIdList,user2songRatingMatrix,3);
	//使用协同过滤，计算推荐
	user2songRecMatrix=CollaborativeFiltering.userKNNBasedCF(userIdList, userKNNMatrix, user2songRatingMatrix, songIdList, 10);
	System.out.println(user2songRecMatrix);
	
	//值得注意的是，插入之前要先清空数据的
	saveRecResult(userIdList, user2songRecMatrix);
	return user2songRecMatrix;
	}
	
	public boolean saveRecResult(List<Integer> userIdList, Map<Integer,Integer[]> user2songRecMatrix)
	{
		//根据每一个用户的id去遍历，遍历一次，取出推荐的数组，然后遍历数组进行一个个的插入
		userIdList.forEach(new Consumer<Integer>() {

			@Override
			public void accept(Integer curUserId) {
				// TODO Auto-generated method stub
				Integer[] curUserRecSongId=user2songRecMatrix.get(curUserId);
				//遍历数组进行保存啦
				for(int i=0;i<curUserRecSongId.length;i++)
				{
					//取出songid
					Integer SongId=curUserRecSongId[i];
					//curUserId，下面进行数据的插入吧
					String sql="Insert Into userrec(userId,songId,Date) values(?,?,?)";
					try {
						Connection connection=GetSqlConn.GetConn();
						PreparedStatement preparedStatement=connection.prepareStatement(sql);
						preparedStatement.setInt(1, curUserId);
						preparedStatement.setInt(2, SongId);
						
						//获取时间并保存
						String utilDate=null;
						Calendar cal = Calendar.getInstance();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						utilDate=formatter.format(cal.getTime());
	
						preparedStatement.setString(3, utilDate);
						preparedStatement.execute();
						
						connection.close();
						preparedStatement.close();
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
				}
			}
		});
		return true;
	}
	//新用户歌单的推荐
	public void run_firstSongListRec(int userid)
	{
		//开始
		//用户-歌曲 推荐列表
		Map<Integer,Integer[]> user2songListRecMatrix=new HashMap<Integer, Integer[]>();
		
		//获取用户
		List<Integer> userIdList=DataTranslate.GetuserIdList();
		//获取歌单类型
		List<Integer> typeIdList=DataTranslate.GetTypeIdList();
		//获取用户评分矩阵
		Map<Integer, double[]> user2songRatingMatrix=DataTranslate.GetUserTimesMatrix(userIdList, typeIdList);
		//计算用户相似度，获取用户k个近邻用户
		Map<Integer,Integer[]> userKNNMatrix=UserKNN.getKNN(userIdList,user2songRatingMatrix,3);
		//使用协同过滤，计算推荐
		Integer[] singleSongListRec= CollaborativeFiltering.singleUserRec(userIdList, userKNNMatrix, user2songRatingMatrix, typeIdList, 10, userid);
		System.out.println(user2songListRecMatrix);
		
		//值得注意的是，插入之前要先清空数据的
		saveSingleSongListRecResult(userid, singleSongListRec);
	}
	

	//关于歌单的推荐
	public Map<Integer,Integer[]> run_songListRec()
	{
	//开始
	//用户-歌曲 推荐列表
	Map<Integer,Integer[]> user2songListRecMatrix=new HashMap<Integer, Integer[]>();
	
	//获取用户
	List<Integer> userIdList=DataTranslate.GetuserIdList();
	//获取歌单类型
	List<Integer> typeIdList=DataTranslate.GetTypeIdList();
	//获取用户评分矩阵
	Map<Integer, double[]> user2songRatingMatrix=DataTranslate.GetUserTimesMatrix(userIdList, typeIdList);
	//计算用户相似度，获取用户k个近邻用户
	Map<Integer,Integer[]> userKNNMatrix=UserKNN.getKNN(userIdList,user2songRatingMatrix,3);
	//使用协同过滤，计算推荐
	user2songListRecMatrix=CollaborativeFiltering.userKNNBasedCF(userIdList, userKNNMatrix, user2songRatingMatrix, typeIdList, 10);
	System.out.println(user2songListRecMatrix);
	
	//值得注意的是，插入之前要先清空数据的
	saveSongListRecResult(userIdList, user2songListRecMatrix);
	return user2songListRecMatrix;
	}
	
	
	//保存单个用户的推荐歌单结果
	private void saveSingleSongListRecResult(int userid, Integer[] curUserRecSongId) {
			// TODO Auto-generated method stub
		for(int i=0;i<curUserRecSongId.length;i++)
		{
			//取出songid
			Integer SongId=curUserRecSongId[i];
			//curUserId，下面进行数据的插入吧
			String sql="Insert Into userrectype(userId,typeId,Date) values(?,?,?)";
			try {
				Connection connection=GetSqlConn.GetConn();
				PreparedStatement preparedStatement=connection.prepareStatement(sql);
				preparedStatement.setInt(1, userid);
				preparedStatement.setInt(2, SongId);
				
				//获取时间并保存
				String utilDate=null;
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				utilDate=formatter.format(cal.getTime());

				preparedStatement.setString(3, utilDate);
				preparedStatement.execute();
				
				connection.close();
				preparedStatement.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		}
	}
	
	public boolean saveSongListRecResult(List<Integer> userIdList, Map<Integer,Integer[]> user2songListRecMatrix)
	{
		//根据每一个用户的id去遍历，遍历一次，取出推荐的数组，然后遍历数组进行一个个的插入
		userIdList.forEach(new Consumer<Integer>() {

			@Override
			public void accept(Integer curUserId) {
				// TODO Auto-generated method stub
				Integer[] curUserRecSongId=user2songListRecMatrix.get(curUserId);
				//遍历数组进行保存啦
				for(int i=0;i<curUserRecSongId.length;i++)
				{
					//取出songid
					Integer SongId=curUserRecSongId[i];
					//curUserId，下面进行数据的插入吧
					String sql="Insert Into userrectype(userId,typeId,Date) values(?,?,?)";
					try {
						Connection connection=GetSqlConn.GetConn();
						PreparedStatement preparedStatement=connection.prepareStatement(sql);
						preparedStatement.setInt(1, curUserId);
						preparedStatement.setInt(2, SongId);
						
						//获取时间并保存
						String utilDate=null;
						Calendar cal = Calendar.getInstance();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						utilDate=formatter.format(cal.getTime());
	
						preparedStatement.setString(3, utilDate);
						preparedStatement.execute();
						
						connection.close();
						preparedStatement.close();
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
				}
			}
		});
		return true;
	}
	
}
