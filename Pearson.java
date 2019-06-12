package com.wyb.rec.CollaborationFiltering;
/**
 * 皮尔逊指数的计算，用来计算用户之间相似度的
 * @author wyb
 *
 */
public class Pearson {
	public static double Sum(double[] arr){
		double total=(double)0.0;
		for(double ele:arr)
			total+=ele;
		return total;
	}
	public static double Mutipl(double[] arr1,double[] arr2,int len){
		double total=(double)0.0;
		for(int i=0;i<len;i++)
			total+=arr1[i]*arr2[i];
		return total;
	}
	public static double Pearson(double[] x,double[] y){
		int lenx=x.length;//x长度
		int leny=y.length;//y长度
		int len=lenx;//小容错
		if(lenx<leny) len=lenx;//len作为长度小的
		else len=leny;	
		double sumX=Sum(x);//x总和
		double sumY=Sum(y);//y总和
		double sumXX=Mutipl(x,x,len);//x^2
		double sumYY=Mutipl(y,y,len);//y^2
		double sumXY=Mutipl(x,y,len);//x*y
		double upside=sumXY-sumX*sumY/len;
		//double downside=(double) Math.sqrt((sumXX-(Math.pow(sumX, 2))/len)*(sumYY-(Math.pow(sumY, 2))/len));
		double downside=(double) Math.sqrt((sumXX-Math.pow(sumX, 2)/len)*(sumYY-Math.pow(sumY, 2)/len));
		
		//System.out.println(len+" "+sumX+" "+sumY+" "+sumXX+" "+sumYY+" "+sumXY);
		return upside/downside;
	}
}
