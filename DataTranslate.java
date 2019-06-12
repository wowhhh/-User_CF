package com.wyb.rec.CollaborationFiltering;
/**
 * 主要功能：读取数据库进行数据的转换
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
/**
 * 用于从数据库中读取数据然后做成矩阵
 * 用户id
 * 歌曲id
 * 用户-歌曲评分矩阵
 * @author wyb
 *
 */
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Test;

import com.sun.tracing.dtrace.ProviderAttributes;
import com.wyb.rec.utils.SavaSongList.GetConn;
public class DataTranslate {
	private static Statement statement=null;
	private static Connection conn=null;
	private static ResultSet RateSet=null;
	private static ResultSet resultSet=null;
	/**
	 * 需要返回的数据：
	 * @return userIdList 用户表
	 * @return songIdList 歌曲表
	 * @return user-songRatingMatrix 用户歌曲评分矩阵
	 * @throws SQLException 
	 */
	
	
	
	//获取用户id表 bingo
	
	public static  List<Integer> GetuserIdList () 
	{
		List<Integer> userIdList =new LinkedList<Integer>();
		String sql="select iduser from user";
		try {
			statement=GetSqlConn.GetStat();
		
		resultSet=statement.executeQuery(sql);
		
		while(resultSet.next())
		{
			int x=resultSet.getInt(1);
			userIdList.add( x);
			
		}
		statement.close();
		

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(userIdList);
		return userIdList;
	}
	
	//获取歌曲id表
	public static List<Integer> GetsongIdList() 
	{
		List<Integer> songIdList=new LinkedList<Integer>();
		//String sql="select * from song";//更换语句，音乐好像没有很完整的插入，中间有间隔，所以查询最后一个id
		String sql="select * from song order by idsong DESC limit 1";
		try {
			statement=GetSqlConn.GetStat();
		
		resultSet=statement.executeQuery(sql);
		int index=1;
		if(resultSet.next())
		{
			
		
		for(index=1;index<=resultSet.getInt(1);index++)
		{
			songIdList.add(index);
		}
		
		
		}
		statement.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(songIdList);
		
		return songIdList;
	}
	
	//获取用户-歌曲评分矩阵
	public static Map<Integer, double[]> GetUserItemMatrix(List<Integer> userIdList,List<Integer> songIdList)
	{
		
		int songLen=songIdList.size();
		final Map<Integer,double[]> user2songRatingMatrix=new HashMap<Integer, double[]>();
		
		userIdList.forEach(new Consumer<Integer>() {

			@Override
			public void accept(Integer userId) {
				// TODO Auto-generated method stub
				double[] curUserRatingArray=new double[songLen+1];//当前用户评分的数组大小
				for(int i=0;i<songLen;i++)
				{
				curUserRatingArray[i]=0.0;
				}
				
				try
				{
				String GetData="Select * from rating";//写sql语句
				statement=GetSqlConn.GetStat();
				 RateSet=statement.executeQuery(GetData);//获取查询得到的结果集
				//如果当前进行的用户是数据库里面的
				
					while(RateSet.next())
					{
					
						if(userId==RateSet.getInt(2))
						{
							    //下面是保存每个数据
						
								//歌曲在数组中的位置是RateSet.getInt(3)
							    System.out.println(RateSet.getInt(3));
							    System.out.println(RateSet.getInt(4));
								curUserRatingArray[RateSet.getInt(3)]=RateSet.getInt(4);//保存数据啦	
								
						}
					}
					statement.close();

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				user2songRatingMatrix.put(userId, curUserRatingArray);
			}
			
		});
		
		
		return user2songRatingMatrix;
	}
	
	//下面是关于歌单的推荐：用户id表已经获取完成，下面获取类型id表，获取用户次数矩阵，然后开始计算
	public static List<Integer> GetTypeIdList()
	{
		List<Integer> typeIdList=new LinkedList<Integer>();
		//String sql="select * from song";//更换语句，音乐好像没有很完整的插入，中间有间隔，所以查询最后一个id
		String sql="select * from type ";
		try {
			statement=GetSqlConn.GetStat();
		
	    resultSet=statement.executeQuery(sql);
		int index=1;
		while(resultSet.next())
		{
			typeIdList.add(resultSet.getInt(1));
		}
		statement.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(typeIdList);
		
		return typeIdList;
	}
	//获取用户-次数矩阵
	public static Map<Integer, double[]> GetUserTimesMatrix(List<Integer> userIdList,List<Integer> typeIdList)
	{
		
		int songLen=typeIdList.size();
		final Map<Integer,double[]> user2typeTimesMatrix=new HashMap<Integer, double[]>();
		
		userIdList.forEach(new Consumer<Integer>() {

			@Override
			public void accept(Integer userId) {
				
				// TODO Auto-generated method stub
				double[] curUserRatingArray=new double[songLen+1];//当前用户评分的数组大小
				for(int i=0;i<songLen;i++)
				{
				curUserRatingArray[i]=0.0;
				}
				
				try
				{
				Class.forName("com.mysql.jdbc.Driver");
				String url="jdbc:mysql://localhost:3306/doublerec";//连接本地数据库
					//String url="jdbc:mysql://101.132.40.184:3306/musicRe";//连接云端数据库
				 conn = DriverManager.getConnection(url,"root", "sspu");
					
				String GetData="Select * from userandsltype";//写sql语句
				statement=conn.createStatement();
				 RateSet=statement.executeQuery(GetData);//获取查询得到的结果集
				//如果当前进行的用户是数据库里面的
				
					while(RateSet.next())
					{
					
						if(userId==RateSet.getInt(2))
						{
							    //下面是保存每个数据
						
								//歌曲在数组中的位置是RateSet.getInt(3)
							    System.out.println(RateSet.getInt(3));
							    System.out.println(RateSet.getInt(4));
								curUserRatingArray[RateSet.getInt(3)]=RateSet.getInt(4);//保存数据啦	
								
						}
					}
					statement.close();
					conn.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally {
					 try {
				            if(RateSet !=null)RateSet.close();
				        } catch (Exception e) {
				          
				        }

				        try {
				            if(statement !=null)statement.close();
				        } catch (Exception e) {
				            
				        }

				        try {
				            if(conn !=null)conn.close();
				        } catch (Exception e) {
				            
				        }

				}
				
				user2typeTimesMatrix.put(userId, curUserRatingArray);
			}
			
		});
		
		
		return user2typeTimesMatrix;
	}
}
