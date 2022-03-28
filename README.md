# HashAlgo :lock:
#### Second Attempt to Understand Hashing.
## Example of usage :snail:
```java
public class Main
{
  public static void main(String[] args)
  {
    byte[] data = "Hello, world!".getBytes();
    byte[] hash = new HashAlgo().get(data);
    
    System.out.println(bytesToHex(hash));
  }

  /* helper function to visualize the hash */
  static String bytesToHex(byte[] bytes)
  {
    StringBuilder hexString = new StringBuilder();

    for (int i = 0; i < bytes.length; i++)
    {   
      String hex = Integer.toHexString(0xFF & bytes[i]);
      hexString.append(hex.length() == 1 ? "0" : hex);
    }

    return hexString.toString();
  }
}
```
