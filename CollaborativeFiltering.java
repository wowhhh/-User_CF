package com.wyb.rec.CollaborationFiltering;
import java.util.HashMap;
import java.util.List;
/**
 * 重点
 * @author wyb
 *
 */
import java.util.Map;
import java.util.function.Consumer;
public class CollaborativeFiltering {
		/**
		 	* 基于用户的协同过滤的推荐结果
		 	* @param userIdList 用户id列表
		 	* @param userKNNMatrix 用户KNN矩阵
		 	* @param user2songRatingMatrix 用户-歌曲评分矩阵
		 	* @param songIdList 歌曲id列表
		 	* @param n 推荐的前n首歌曲
		 	* @return 对用户的歌曲推荐的结果  userId,[recSongidId1,recSongidId2]
		 	* 
		*/
	public static void test()
	{
		
	}
	//要先查询出用户的id，输出结果是为用户推荐的一维矩阵，此方法仅用户歌单的推荐
	public static Integer[] singleUserRec(List<Integer> userIdList,
			final Map<Integer,Integer[]> userKNNMatrix,final Map<Integer, double[]> user2songRatingMatrix,
		final List<Integer> songIdList,final int n,int curUserId)
	{
		// TODO Auto-generated method stub
		Integer[] knnIdArray=userKNNMatrix.get(curUserId);//定义数组用来保存目标用户的近邻用户的id
		/**
		 * 对于每一首当前用户没有听过的歌曲
		 * 协同得分：其k个最近邻用户对该歌曲的评分的聚合
		 */
		//定义一个用来存储目标用户对所有歌曲评分的数组
		double[] curUserRatings=user2songRatingMatrix.get(curUserId);
		//定义一个保存目标用户歌曲的最小堆
		MininumHeap mininumHeap=new MininumHeap(n);
		//循环处理目标用户没有听过的歌曲
		for(int i=1;i<curUserRatings.length;i++)
		{
			/**浮点数无法直接==比大小**/
			if(curUserRatings[i]<0.01f)//如果目标用户对此歌曲没有过行为
			{
				//循环处理邻近用户
				for(int knnIndex=0;knnIndex<knnIdArray.length;knnIndex++)
				{
					int knnId=knnIdArray[knnIndex];//获取近邻用户在用户列表中的id
					double[] knnUserRatings=user2songRatingMatrix.get(knnId);//获取近邻用户对所有歌曲的评分
					curUserRatings[i]+=knnUserRatings[i];//暂且保存总分
				}
				//总分计算完成之后，计算平均值作为预测
				curUserRatings[i]/=knnIdArray.length;
				int curSongId=songIdList.get(i-1);
				//放入到堆中
				mininumHeap.addElement(new TreeNode(curSongId,curUserRatings[i]));
			}
		}
		/**
		 * 对该用户没有听过的歌曲，协同得分完成，选取n个作为最高的项目进行推荐
		 */
		int trueNumber=n;
		//如果推荐的数目少于计划推荐的数目（处理的歌曲情况很少）
		if(mininumHeap.getCurHeapSize()<n)
		{
			trueNumber=mininumHeap.getCurHeapSize();
		}
		//定义推荐歌曲id的数组，大小为truenumber
		Integer[] curUserRecSongId=new Integer[trueNumber];
		for(int i=0;i<trueNumber;i++)
		{
			int recSongId=mininumHeap.getArray()[i].id;//获取推荐的歌曲的id
			curUserRecSongId[i]=recSongId;
		}
		
		//有用户id以及矩阵，插入即可
		
		return curUserRecSongId;
		
		//此处可以直接保存到数组当中
	}
	
	//只计算单个用户的推荐结果，传入用户的id
	public static Map<Integer,Integer[]> userKNNBasedCF( List<Integer> userIdList,
			final Map<Integer,Integer[]> userKNNMatrix,final Map<Integer, double[]> user2songRatingMatrix,
		final List<Integer> songIdList,final int n	)
	{
		//定义用户的推荐歌曲的矩阵,形如:[用户id] [推荐歌曲1，推荐歌曲2，推荐歌曲3]
		final Map<Integer, Integer[]> user2songRecMatrix=new HashMap<Integer,Integer[]>();
		
		//根据所有的用户id，进行遍历
		userIdList.forEach(new Consumer<Integer>() {

			@Override
			public void accept(Integer curUserId) {
				// TODO Auto-generated method stub
				Integer[] knnIdArray=userKNNMatrix.get(curUserId);//定义数组用来保存目标用户的近邻用户的id
				/**
				 * 对于每一首当前用户没有听过的歌曲
				 * 协同得分：其k个最近邻用户对该歌曲的评分的聚合
				 */
				//定义一个用来存储目标用户对所有歌曲评分的数组
				double[] curUserRatings=user2songRatingMatrix.get(curUserId);
				//定义一个保存目标用户歌曲的最小堆
				MininumHeap mininumHeap=new MininumHeap(n);
				//循环处理目标用户没有听过的歌曲
				for(int i=1;i<curUserRatings.length;i++)
				{
					/**浮点数无法直接==比大小**/
					if(curUserRatings[i]<0.01f)//如果目标用户对此歌曲没有过行为
					{
						//循环处理邻近用户
						for(int knnIndex=0;knnIndex<knnIdArray.length;knnIndex++)
						{
							int knnId=knnIdArray[knnIndex];//获取近邻用户在用户列表中的id
							double[] knnUserRatings=user2songRatingMatrix.get(knnId);//获取近邻用户对所有歌曲的评分
							curUserRatings[i]+=knnUserRatings[i];//暂且保存总分
						}
						//总分计算完成之后，计算平均值作为预测
						curUserRatings[i]/=knnIdArray.length;
						int curSongId=songIdList.get(i-1);
						//放入到堆中
						mininumHeap.addElement(new TreeNode(curSongId,curUserRatings[i]));
					}
				}
				/**
				 * 对该用户没有听过的歌曲，协同得分完成，选取n个作为最高的项目进行推荐
				 */
				int trueNumber=n;
				//如果推荐的数目少于计划推荐的数目（处理的歌曲情况很少）
				if(mininumHeap.getCurHeapSize()<n)
				{
					trueNumber=mininumHeap.getCurHeapSize();
				}
				//定义推荐歌曲id的数组，大小为truenumber
				Integer[] curUserRecSongId=new Integer[trueNumber];
				for(int i=0;i<trueNumber;i++)
				{
					int recSongId=mininumHeap.getArray()[i].id;//获取推荐的歌曲的id
					curUserRecSongId[i]=recSongId;
				}
				user2songRecMatrix.put(curUserId,curUserRecSongId);
			}
		});
		
		return user2songRecMatrix;
	}
}
