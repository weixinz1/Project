public class test{
 public static void main(String[] args){
int[] h=extend_gcd(6,5);
System.out.println(h[0]+" "+h[1]+" "+h[2]);

 }

public static int[] extend_gcd(int a,int b){  
        int ans;  
        int[] result=new int[3];  
        if(b==0)  
        {  
            result[0]=a;  
            result[1]=1;  
            result[2]=0;  
            return result;  
        }  
        int [] temp=extend_gcd(b,a%b);  
        ans = temp[0];  
        result[0]=ans;  
        result[1]=temp[2];  
        result[2]=temp[1]-(a/b)*temp[2];  
        return result;  
}
}