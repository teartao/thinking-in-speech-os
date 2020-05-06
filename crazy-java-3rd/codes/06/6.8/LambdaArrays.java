
import java.util.Arrays;
/**
 * Description:
 * <br/>��վ: <a href="http://www.crazyit.org">���Java����</a>
 * <br/>Copyright (C), 2001-2016, Yeeku.H.Lee
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:
 * <br/>Date:
 * @author Yeeku.H.Lee kongyeeku@163.com
 * @version 1.0
 */
public class LambdaArrays
{
	public static void main(String[] args)
	{
		String[] arr1 = new String[]{"java" , "fkava" , "fkit", "ios" , "android"};
		Arrays.parallelSort(arr1, (o1, o2) -> o1.length() - o2.length());
		System.out.println(Arrays.toString(arr1));
		int[] arr2 = new int[]{3, -4 , 25, 16, 30, 18};
		// left����������ǰһ������������Ԫ�أ������һ��Ԫ��ʱ��leftΪ1
		// right���������е�ǰ��������Ԫ��
		Arrays.parallelPrefix(arr2, (left, right)-> left * right);
		System.out.println(Arrays.toString(arr2));
		long[] arr3 = new long[5];
		// operand�������ڼ����Ԫ������
		Arrays.parallelSetAll(arr3 , operand -> operand * 5);
		System.out.println(Arrays.toString(arr3));
	}
}