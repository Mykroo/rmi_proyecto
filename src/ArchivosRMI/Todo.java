/*File streaming using Java RMI

In client-server applications there's often the need to transfer files between both parties. This blog shows how to implement a file transfer using remote capable input and output streams and how to improve the performance in WAN scenarios where network connections even with good bandwidth still suffer from high latency.
Java Remote Method Invocation (RMI) is a very easy and proven way to implement client-server or process-process communication. And since Java 1.5 stubs and skeletons are created automatically using dynamic proxies without need to deal with the RMI compiler tool (rmic).
Sample Server using an embedded RMI Registry:
*/
public class TestServer {

    public interface Server extends Remote {
        public String sayHello() throws RemoteException;
    }
    
    public static class ServerImpl extends UnicastRemoteObject
            implements Server {

        Registry rmiRegistry;

        public ServerImpl() throws RemoteException {
            super();
        }

        public void start() throws Exception {
            rmiRegistry = LocateRegistry.createRegistry(1099);
            rmiRegistry.bind("server", this);
            System.out.println("Server started");
        }

        public void stop() throws Exception {
            rmiRegistry.unbind("server");
            unexportObject(this, true);
            unexportObject(rmiRegistry, true);
            System.out.println("Server stopped");
        }
        
        public String sayHello() {
            return "Hello world";
        }
        
    }

    public static void main(String[] args) throws Exception {
        ServerImpl server = new ServerImpl();
        server.start();
        Thread.sleep(5 * 60 * 1000); // run for 5 minutes
        server.stop();
    }
}
// Sample Client implementation:

public class TestClient {
    
    public static void main(String[] args) throws Exception {
        
        String url = "rmi://localhost/server";
        Server server = (Server) Naming.lookup(url);

        System.out.println("Server says: " + server.sayHello());
    }
}
// RMI works using call by value. Instances are serialized if they are not itself remote (able) objects (implement the Remote interface), in the later case a reference is transferred.
// Much of the Java IO APIs use InputStreams and OutputStreams. Now how can RMI be used to allow for remote streaming? InputStream and OutputStream are abstract classes and only a few methods have to be implemented (a full implementation may add methods like skip, mark, flush, etc. as well):
// InputStream.read()
// InputStream.read(byte[] b, int off, int len)
// InputStream.close()

// OutputStream.write()
// OutputStream.write(byte[] b, int off, int len)
// OutputStream.close()
// OutputStream is easy since it already complies with RMI:
public interface RMIOutputStreamInterf extends Remote {
    
    public void write(int b) throws IOException, RemoteException;
    public void write(byte[] b, int off, int len) throws 
    IOException, RemoteException;
    public void close() throws IOException, RemoteException;
}
// Input parameters cannot be modified so InputStream.read(byte[] b, int off, int len) doesn't work. A RMI suitable variant could be:
public interface RMIInputStreamInterf extends Remote {
    
    public byte[] readBytes(int len) throws IOException, 
    RemoteException;
    public int read() throws IOException, RemoteException;
    public void close() throws IOException, RemoteException;
}
// To get back to standard InputStream and OutputStream classes, we wrap these RMI classes into serializable classes:
public class RMIInputStream extends InputStream implements Serializable {

    RMIInputStreamInterf in;
    
    public RMIInputStream(RMIInputStreamInterf in) {
        this.in = in;
    }
    
    public int read() throws IOException {
        return in.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        byte[] b2 = in.readBytes(len);
        if (b2 == null)
            return -1;
        int i = b2.length;
        System.arraycopy(b2, 0, b, off, i);
        return i;
    }
    
    public void close() throws IOException {
        super.close();
    }   
}

public class RMIOutputStream extends OutputStream implements Serializable {

    private RMIOutputStreamInterf out;
    
    public RMIOutputStream(RMIOutputStreamImpl out) {
        this.out = out;
    }
    
    public void write(int b) throws IOException {
        out.write(b);
    }

    public void write(byte[] b, int off, int len) throws 
            IOException {
        out.write(b, off, len);
    }
    
    public void close() throws IOException {
        out.close();
    }   
}
// For the users, these classes look like and act like standard input and output streams. Well, they may throw RemoteExeptions, but luckily RemoteExeptions are derived from IOExceptions which have to be handled anyway.
// RMIInputStreamInterf and RMIOutputStreamImpl wrap given input and output streams and export themselves as remote objects:
public class RMIOutputStreamImpl implements RMIOutputStreamInterf {

    private OutputStream out;
    
    public RMIOutputStreamImpl(OutputStream out) throws 
            IOException {
        this.out = out;
        UnicastRemoteObject.exportObject(this, 1099);
    }
    
    public void write(int b) throws IOException {
        out.write(b);
    }

    public void write(byte[] b, int off, int len) throws 
            IOException {
        out.write(b, off, len);
    }

    public void close() throws IOException {
        out.close();
    }
}

public class RMIInputStreamImpl implements RMIInputStreamInterf {

    private InputStream in;
    private byte[] b;

    public RMIInputStreamImpl(InputStream in) throws IOException {
        this.in = in;
        UnicastRemoteObject.exportObject(this, 1099);
    }

    public void close() throws IOException, RemoteException {
        in.close();
    }

    public int read() throws IOException, RemoteException {
        return in.read();
    }

    public byte[] readBytes(int len) throws IOException, 
            RemoteException {
        if (b == null || b.length != len)
            b = new byte[len];
            
        int len2 = in.read(b);
        if (len2 &lt; 0)
            return null; // EOF reached
        
        if (len2 != len) {
            // copy bytes to byte[] of correct length and return it
            byte[] b2 = new byte[len2];
            System.arraycopy(b, 0, b2, 0, len2);
            return b2;
        }
        else
            return b;
    }
}
// Now we can add two methods to our Server and SeverImpl:
public OutputStream getOutputStream(File f) throws IOException {
    return new RMIOutputStream(new RMIOutputStreamImpl(new 
    FileOutputStream(f)));
}

public InputStream getInputStream(File f) throws IOException {
    return new RMIInputStream(new RMIInputStreamImpl(new 
    FileInputStream(f)));
}
// Sample to use it:
public class TestClient {
    
    final public static int BUF_SIZE = 1024 * 64;
    
    public static void copy(InputStream in, OutputStream out) 
            throws IOException {
        System.out.println("using byte[] read/write");
        byte[] b = new byte[BUF_SIZE];
        int len;
        while ((len = in.read(b)) &gt;= 0) {
            out.write(b, 0, len);
        }
        in.close();
        out.close();
    }
    
    public static void upload(ServerInterf server, File src, 
            File dest) throws IOException {
        copy (new FileInputStream(src), 
        server.getOutputStream(dest));
    }

    public static void download(ServerInterf server, File src, 
            File dest) throws IOException {
        copy (server.getInputStream(src), 
        new FileOutputStream(dest));
    }

    public static void main(String[] args) throws Exception {
        
        String url = "rmi://localhost/server";
        ServerInterf server = (ServerInterf) Naming.lookup(url);

        System.out.println("Server says: " + server.sayHello());
        
        File testFile = new File("Test100MB.tif");
        long len = testFile.length();
        
        long t;
        t = System.currentTimeMillis();
        download(server, testFile, new File("download.tif"));
        t = (System.currentTimeMillis() - t) / 1000;
        System.out.println("download: " + (len / t / 1000000d) + 
            " MB/s");
        
        t = System.currentTimeMillis();
        upload(server, new File("download.tif"), 
        new File("upload.tif"));
        t = (System.currentTimeMillis() - t) / 1000;
        System.out.println("upload: " + (len / t / 1000000d) + 
            " MB/s");
    }
}
// Notes
// The concept even allows for daisy-chaining: client requests a stream from server 1 which in turn requests the stream from server 2.
// For on the fly data compression you can insert the deflater/inflater stream classes from java.util.zip (since Java 1.6 the implementation is symmetrical providing deflaters and inflaters for both input and output streams).
// UnicastRemoteObject.exportObject(this, 1099) is worth mentioning for two things: It's possible to export objects on the same port as the RMI registry which limits the number of holes to punch through firewalls. And it automatically creates proxy stub classes which UnicastRemoteObject.exportObject(this) wouldn't.
// Still for file transfer the implementation has a drawback: The copy method reads and writes blocks and either call is a remote call. On a LAN that's no big issue, but with ADSL-connections we have latencies of around 50ms or more. Even with infinite bandwidth, to transfer 100MB using 64 KB blocks would cost us 80s assuming a latency of 50ms. A larger block size improves the performance, but we shouldn't waste memory.
// The trick to improve performance is to perform the file transfer "inline" as part of the RMI serialization.
public class RMIPipe implements Serializable {

    final public static int BUF_SIZE = 1024 * 64;
    private static int keySeed = 0;
    private static Hashtable&lt;Integer,OutputStream&gt; 
    registry = new Hashtable&lt;Integer,OutputStream&gt;();

    private transient int key;
    private transient InputStream in;
    private transient boolean isOutputRegistration;

    public RMIPipe(int key, InputStream in) {
        this.key = key;
        this.in = in;
        isOutputRegistration = false;
    }

    public RMIPipe(OutputStream out) {
        isOutputRegistration = true;
        synchronized (registry) {
            key = keySeed++;
            registry.put(key, out);
        }
    }

    public int getKey() {
        if (!isOutputRegistration)
            throw new IllegalArgumentException(
                "not an OutputStream registration");
        return key;
    }

    protected void finalize() {
        // just to be sure
        if (isOutputRegistration)
            registry.remove(key);
    }

    private void writeObject(ObjectOutputStream out) throws 
            IOException {
        out.writeInt(key);
        byte[] b = new byte[BUF_SIZE];
        int len;
        do {
            len = in.read(b);
            out.writeInt(len);
            if (len &gt;= 0)
                out.write(b, 0, len);
        } while (len &gt;= 0);
    }

    private void readObject(ObjectInputStream in) throws 
            IOException {
        int key = in.readInt();
        OutputStream out = registry.remove(key);
        byte[] b = new byte[BUF_SIZE];
        int len;
        do {
            len = in.readInt();
            if (len &gt;= 0) {
                in.readFully(b, 0, len);
                out.write(b, 0, len);
            }
        } while (len &gt;= 0);
    }

}
// RMIPipe is a serializable object that during serialization transfers data from an input to an output stream. RMIPipe does its own serialization by overriding the writeObject and readObject methods. The serialization reads data from an InputStream instead of serializing some class variables, the deserialization writes data to an OuputStream.
// Actually it's two implementations in one. 
// Initialized on a (local) OutputStream, it will register the OutputStream locally in a static registry. The registration key will be delivered to the remote side. The remote side (InputStream side) then creates an appropriate RMIPipe object and sends it back.
// Initialized on a (local) InputStream, it will remember the stream and serialize it if the RMIPipe instance is serialized.
// Adding to RMInputStreamImpl:
public RMIPipe transfer(int key) throws IOException, RemoteException {
    return new RMIPipe(key, in);
}
//Adding to RMIOutputStreamImpl:
private RMIPipe pipe;
  
public RMIOutputStreamImpl(OutputStream out) throws IOException {
    this.out = out;
    this.pipe = new RMIPipe(out);
    UnicastRemoteObject.exportObject(this, 1099);
}

public int getPipeKey() {
    return pipe.getKey();
}
  
public void transfer(RMIPipe pipe) throws IOException {
    // nothing more to do here
    // pipe has been serialized and that's all we want
}
//Adding to RMIInputStream:
public void transfer(OutputStream out) throws IOException {
    RMIPipe pipe = new RMIPipe(out);
    in.transfer(pipe.getKey());
}
// Adding to RMIOutputStream:
private int pipeKey;
public void transfer(InputStream in) throws IOException {
		RMIPipe pipe = new RMIPipe(pipeKey, in);
		out.transfer(pipe);
}
// The optimized copy method of the TestClient now looks like:
public static void copy(InputStream in, OutputStream out)throws IOException {
    
    if (in instanceof RMIInputStream) {
        System.out.println("using RMIPipe of RMIInputStream");
        ((RMIInputStream) in).transfer(out);
        return;
    }
    
    if (out instanceof RMIOutputStream) {
        System.out.println("using RMIPipe of RMIOutputStream");
        ((RMIOutputStream) out).transfer(in);
        return;
    }

    System.out.println("using byte[] read/write");
    byte[] b = new byte[BUF_SIZE];
    int len;
    while ((len = in.read(b)) &gt;= 0) {
        out.write(b, 0, len);
    }
    in.close();
    out.close();
}