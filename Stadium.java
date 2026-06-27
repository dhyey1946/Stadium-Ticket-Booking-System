import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.io.*;
import java.sql.*;
public class Stadium {
    public static void main(String[] args)throws Exception{
        Scanner sc = new Scanner(System.in);
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/stadium_project","root","");
        SetUser s = new SetUser();
        Staff staff = new Staff();
        LoginedUSer lu = new LoginedUSer();
        if(con!=null)
        {
            System.out.println("============================================================");
            System.out.println("================ GUJARAT CRICKET ASSOCIATION ================");
            System.out.println("============================================================");
            while (true) {
                System.out.println();
                System.out.println("========= Home Page =========");
                System.out.println("1. Staff Login.");
                System.out.println("2. User Registration.");
                System.out.println("3. User Login.");
                System.out.println("4. EXIT");
                int choice=0;
                boolean b = true;
                while (b) {
                    try
                    {
                        System.out.print("Enter Choice : ");
                        choice = sc.nextInt();
                        b=false;
                    }
                    catch(Exception e)
                    {
                        System.out.println("Invalid Choice , Please Enter Valid Choice .");
                        sc.next();
                    }
                }
                switch (choice) {
                    case 1:
                        staff.loginStaff(con);
                        break;
                    case 2:
                        s.newUser(con);
                        break;
                    case 3:
                        lu.loginUser(con);
                        break;
                    case 4:
                        System.out.println("----- Thank You For Visit -----");
                        System.exit(0);
                    default:
                        System.out.println("Invalid Choice , Enter Valid Choice");
                        sc.next();
                        break;
                }
            }
        }
        sc.close();
    }
}
class Match
{
    int match_id;
    String match;
    String match_date;
    String match_time;
    String status;
    public Match(int match_id, String match, String match_date,String match_time, String status) {
        this.match_id = match_id;
        this.match = match;
        this.match_date = match_date;
        this.match_time = match_time;
        this.status = status;
    }
    @Override
    public String toString() {
        return "id : " + match_id + "               Match : " + match + "\nMatch Date : " + match_date + "      Match Time : "
                + match_time + "\nstatus : "+status+"\n";
    }
}
class User
{
    String name;
    int userId;
    String password;
    String mobile;
    public User(String name, int userId, String password, String mobile) {
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.mobile = mobile;
    }
}
class SetUser
{
    Scanner sc=new Scanner(System.in);
    Get g = new Get();
    void newUser(Connection con) throws Exception
    {
        System.out.println("----- Welcome To Registration Page -----");
        String name;
        while(true) {
            System.out.println("Enter User Name : ");
            name = sc.nextLine();
            if(name.matches("[a-zA-Z ]+") && !name.isBlank())
            {
                break;
            }
            else
            {
                System.out.println("Invalid name. Enter alphabets only.");
            }
        }
        int userId = 0;
        boolean b = true;
        while (b) {
            try
            {
                System.out.println("Enter UserId : ");
                userId=sc.nextInt();
                b=false;
            }
            catch(InputMismatchException e)
            {
                System.out.println("Invalid User ID , Please Enter User ID in Integer Data Type.");
                sc.next();
            }
        }
        System.out.println("Enter Password : ");
        String password = sc.next();
        System.out.println("Enter Mobile no. : ");
        String mobile = sc.next();
        int x = 0;
        if(mobile.length()==10)
        {
            for (int i = 0; i < 10; i++) {
                if (mobile.charAt(i)>='0' && mobile.charAt(i)<='9') {
                    x++;
                }
            }
        }
        if(x == 10)
        {
            try
            {
                String insert = "insert into user(user_id,user_name,password,mobile_no) values(?,?,?,?)";
                PreparedStatement pst = con.prepareStatement(insert);
                pst.setInt(1, userId);
                pst.setString(2, name);
                pst.setString(3, password);
                pst.setString(4, mobile);
                int r = pst.executeUpdate();
                if(r>0)
                {
                    Get g = new Get();
                    String dateTime = g.getDateTime();
                    File f=new File("src/"+mobile+".txt");
                    f.createNewFile();
                    BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                    bw.write(dateTime);
                    bw.newLine();
                    bw.write("1 new Message : ");
                    bw.newLine();
                    bw.write("     Your Mobile No. Registered At Narendra Modi Cricket Stadium Web.");
                    bw.newLine();
                    bw.close();
                    System.out.println("------- Registration Success -------");
                }
                else
                {
                    System.out.println(" Sorry , Registration Failed ");
                }
            }
            catch(SQLIntegrityConstraintViolationException e)
            {
                System.out.println("Registration Failed");
                System.out.println("User ID Already exist , Please Register With Different User ID");
            }
        }
        else
        {
            System.out.println("Invalid Mobile No. , Registration Failed");
        }
    }
    void changePassword(Connection con) throws Exception
    {
        System.out.println("Enter UserId : ");
        int userId = sc.nextInt();
        String old_pass="";
        String sql = "select password from user where user_id='"+userId+"'";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        int i=0;
        while (rs.next()) {
            old_pass = rs.getString(1);
            i++;
        }
        rs.close();
        if(i>0)
        {
            System.out.println("Enter Old Password : ");
            String pass = sc.next();
            if(pass.equals(old_pass))
            {
                System.out.println("Enter New Password : ");
                String new_pass=sc.next();
                String update = "update user set password = '"+new_pass+"' where user_id='"+userId+"'";
                PreparedStatement pst1=con.prepareStatement(update);
                int r = pst1.executeUpdate();
                if(r>0)
                {
                    System.out.println("------ Password Changed Successfully ------");
                }
            }
            else
            {
                System.out.println("Password Not Matched , Password Changing Failed");
            }
        }
        else
        {
            System.out.println("No User Id Found");
        }
    }
    void deleteUser(Connection con,int userId) throws Exception
    {
        String sql1 = "select mobile_no from user where user_id="+userId;
        PreparedStatement pst1 = con.prepareStatement(sql1);
        ResultSet rs = pst1.executeQuery();
        while (rs.next()) {
            int otp=(int)(Math.random()*1000000);
            String mob = rs.getString(1);
            File f = new File("src/"+mob+".txt");
            RandomAccessFile file = new RandomAccessFile(f,"rw");
            file.seek(file.length());
            file.writeBytes("\n"+g.getDateTime());
            file.writeBytes("\n1 new Message : ");
            file.writeBytes("\n   Your OTP for Delete User is "+otp+"\n");
            System.out.println("OTP sent to Your Registered Mobile No. "+mob);
            int p=3;
            boolean b = false;
            while (p>0) {
                System.out.println("Enter OTP here : ");
                int fOtp = sc.nextInt();
                if (otp==fOtp) {
                    String sql = "{call delete_user(?)}";
                    CallableStatement cst = con.prepareCall(sql);
                    cst.setInt(1, userId);
                    int r = cst.executeUpdate();
                    if(r>0)
                    {
                        System.out.println("----- User Deleted Successfully -----");
                        b = true;
                    }
                    break;
                }
                else
                {
                    System.out.println("Invalid OTP , Attempt left : "+--p);
                }
            }
            if(!b)
            {
                System.out.println("User Deletion Failed");
            }
            file.close();
        }
    }
}
class LoginedUSer
{
    Scanner sc = new Scanner(System.in);
    void loginUser(Connection con) throws Exception {
        System.out.println("------ Welcome to User Login Page ------");
        int user_id = 0;
        boolean b = false;
        while(!b)
        {
            try
            {
                System.out.print("Enter User id: ");
                user_id = sc.nextInt();
                b = true;
            }
            catch(Exception e)
            {
                System.out.println("Invalid user id");
                sc.next();
            }
        }
        String sql = "SELECT password FROM user WHERE user_id=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, user_id);
        ResultSet rs = pst.executeQuery();

        int i = 0;
        String pass = "";
        while (rs.next()) {
            pass = rs.getString(1);
            i++;
        }

        if (i > 0) {
            int attempts = 3;
            boolean success = false;

            while (attempts > 0) {
                System.out.print("Enter Password : ");
                String password = sc.next();

                if (pass.equals(password)) {
                    System.out.println("----- Login Successful -----");
                    loginedUser(con, user_id);
                    success = true;
                    break;
                } else {
                    attempts--;
                    if (attempts > 0) {
                        System.out.println("❌ Incorrect password. You have " + attempts + " attempt(s) left.");
                    }
                }
            }

            if (!success) {
                System.out.println("⚠️ Too many failed attempts. Redirecting to Home Page...");
                return; // go back to main/home
            }

        } else {
            System.out.println("❌ User ID not found.");
        }
    }

    void loginedUser(Connection con,int userId) throws Exception
    {
        Ticket t = new Ticket();
        SetUser su = new SetUser();
        int choice=0;
        while (choice!=6) {
            System.out.println("-------- Welcome to User Page --------");
            System.out.println("1. Book Ticket");
            System.out.println("2. SMS Tickets to Your Mobile No.");
            System.out.println("3. Get Matches Details ");
            System.out.println("4. Change Password ");
            System.out.println("5. Delete Account");
            System.out.println("6. Go To Home Page");
            boolean b = true;
            while (b) {
                try
                {
                    System.out.println("Enter Choice : ");
                    choice = sc.nextInt();
                    b=false;
                }
                catch(Exception e)
                {
                    System.out.println("Invalid Choice , Please Enter Valid Choice .");
                    sc.next();
                }
            }
            switch (choice) {
                case 1:
                {
                    t.bookTicket(con, userId);
                    break;
                }
                case 2:
                {
                    t.smsTicket(con, userId);
                    break;
                }
                case 3:
                {
                    getMatch(con);
                    break;
                }
                case 4:
                {
                    su.changePassword(con);
                    break;
                }
                case 5:
                {
                    su.deleteUser(con, userId);
                    break;
                }
                case 6:
                {
                    break;
                }
                default:
                {
                    System.out.println("Enter Valid Choice From 1 to 6");
                    break;
                }
            }
        }
    }
    ArrayList<Integer> getMatch(Connection con) throws Exception
    {
        ArrayList<Integer> al = new ArrayList<>();
        String select = "select * from matches";
        PreparedStatement pst = con.prepareStatement(select);
        ResultSet rs = pst.executeQuery();
        System.out.println("------- Available Matches -------");
        LinkedList ll = new LinkedList();
        while (rs.next()) {
            al.add(rs.getInt(1));
            Match m = new Match(rs.getInt(1),rs.getString(2),rs.getString(5),rs.getString(6),rs.getString(7));
            ll.add(m);
        }
        ll.display();
        return al;
    }
}
class Staff
{
    Scanner sc = new Scanner(System.in);
    void loginStaff(Connection con) throws Exception {
        LoginedStaff ls = new LoginedStaff();
        System.out.println("------ Welcome to Staff Login Page ------");

        int staff_id = -1;
        String pass = null;   // better use null for detection
        int staffAttempts = 3;

        while (staffAttempts > 0) {
            System.out.print("Enter Staff id: ");

            // ✅ Validate integer input
            if (!sc.hasNextInt()) {
                sc.next(); // consume invalid input
                staffAttempts--;
                System.out.println("❌ Invalid input! Staff Id must be an integer. Attempts left: " + staffAttempts);
                continue;
            }

            staff_id = sc.nextInt();

            String select = "SELECT password FROM staff WHERE staff_id=?";
            PreparedStatement pst = con.prepareStatement(select);
            pst.setInt(1, staff_id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                pass = rs.getString("password");
                break;
            } else {
                staffAttempts--;
                System.out.println("❌ Staff Id Not Found. Attempts left: " + staffAttempts);
            }
        }
        if (pass == null) {
            System.out.println("🚫 Too many failed attempts. Returning to home page....");
            return;
        }
        int attempts = 3;
        boolean loggedIn = false;
        while (attempts > 0) {
            System.out.print("Enter Password (" + attempts + " attempts left): ");
            String password = sc.next();

            if (pass.equals(password)) {
                System.out.println("----- ✅ Login Successful -----");
                ls.staffFunction(con);
                loggedIn = true;
                break;
            } else {
                attempts--;
                System.out.println("❌ Password Not Matched. Attempts left: " + attempts);
            }
        }

        if (!loggedIn) {
            System.out.println("🚫 Too many failed attempts. Returning to home page....");
        }
    }

}
class LoginedStaff
{
    Scanner sc = new Scanner(System.in);
    void staffFunction(Connection con) throws Exception
    {
        int choice=0;
        while (choice!=8) {
            System.out.println();
            System.out.println("========= Staff Work Page =========");
            System.out.println("1. Add Match");
            System.out.println("2. Remove Match");
            System.out.println("3. Get Last Booked Ticket Details");
            System.out.println("4. Get Match Detail By Match id");
            System.out.println("5. Get User Detail By User id");
            System.out.println("6. Get Revenue Report for match");
            System.out.println("7. Enter match id of completed match");
            System.out.println("8. Go To Home Page");
            boolean b = true;
            while (b) {
                try
                {
                    System.out.println("Enter Choice : ");
                    choice = sc.nextInt();
                    b=false;
                }
                catch(InputMismatchException e)
                {
                    System.out.println("Invalid Choice , Please Enter Valid Choice .");
                    sc.next();
                }
            }
            switch (choice) {
                case 1: {
                    sc.nextLine();
                    System.out.println("Enter Match Name : ");
                    String name = sc.nextLine();
                    int match_id = 0;
                    boolean validId = false;
                    while (!validId) {
                        try {
                            System.out.println("Enter Match Id : ");
                            match_id = sc.nextInt();
                            String checkId = "SELECT COUNT(*) FROM matches WHERE match_id=?";
                            PreparedStatement pstCheckId = con.prepareStatement(checkId);
                            pstCheckId.setInt(1, match_id);
                            ResultSet rsId = pstCheckId.executeQuery();
                            rsId.next();
                            if (rsId.getInt(1) > 0) {
                                System.out.println("❌ Match ID already exists. Enter a different Match ID.");
                            } else {
                                validId = true;
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("❌ Invalid Match ID. Enter a numeric value.");
                            sc.next();
                        }
                    }
                    sc.nextLine();
                    System.out.println("Enter Series or Tournament Name : ");
                    String sName = sc.nextLine();
                    System.out.println("Enter Match Format : ");
                    String match_format = sc.next();
                    String date = "";
                    String time = "";
                    boolean validDateTime = false;
                    int attempts = 3;
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
                    df.setLenient(false);
                    tf.setLenient(false);
                    while (!validDateTime && attempts>0)
                    {
                        try {
                            System.out.println("Enter Match Date (dd/MM/yyyy) : ");
                            date = sc.next();
                            System.out.println("Enter Match Time (HH:mm:ss) : ");
                            time = sc.next();
                            df.parse(date);
                            tf.parse(time);
                            String checkDateTime = "SELECT COUNT(*) FROM matches WHERE match_date=? AND match_time=?";
                            PreparedStatement pstCheckDT = con.prepareStatement(checkDateTime);
                            pstCheckDT.setString(1, date);
                            pstCheckDT.setString(2, time);
                            ResultSet rsDT = pstCheckDT.executeQuery();
                            rsDT.next();
                            if (rsDT.getInt(1) > 0) {
                                System.out.println("❌ A match is already scheduled at this Date & Time. Please enter again.");
                            } else {
                                validDateTime = true;
                            }
                        }
                        catch(Exception e)
                        {
                            attempts--;
                            System.out.println("❌ Invalid format! Date must be yyyy-MM-dd and Time must be HH:mm:ss.");
                            System.out.println("Remaining attempts: " + attempts);
                            sc.nextLine();
                        }
                    }
                    String sql = "INSERT INTO matches(match_id, match_name, series_tournament_name, match_format, match_date, match_time) VALUES (?,?,?,?,?,?)";
                    PreparedStatement pst = con.prepareStatement(sql);
                    pst.setInt(1, match_id);
                    pst.setString(2, name);
                    pst.setString(3, sName);
                    pst.setString(4, match_format);
                    pst.setString(5, date);
                    pst.setString(6, time);
                    int r = pst.executeUpdate();
                    if (r > 0) {
                        System.out.println("------- ✅ Match Added Successfully -------");
                    } else {
                        System.out.println("❌ Sorry, Match Adding Failed.");
                    }
                    break;
                }
                case 2 :
                {
                    System.out.println("Enter Match Id To Remove Match :");
                    int id = sc.nextInt();
                    String sql = "{call delete_match(?)}";
                    CallableStatement cst = con.prepareCall(sql);
                    cst.setInt(1, id);
                    int r = cst.executeUpdate();
                    if(r>0)
                    {
                        System.out.println("----- Match Deleted Successfully For Match id = "+id+" -----");
                    }
                    else
                    {
                        System.out.println("Match id Not Found");
                    }
                    break;
                }
                case 3:
                {
                    System.out.println("Enter how many last booked tickets you want to fetch: ");
                    int n = sc.nextInt();
                    int ticketId;
                    int matchId;
                    int userId;
                    String stand = "";
                    int ticketPrice;
                    int bookedTickets;
                    int totalPaid;
                    String payMethod = "";
                    String select = "select * from ticket";
                    PreparedStatement pst1 = con.prepareStatement(select);
                    ResultSet rs1 = pst1.executeQuery();
                    int i = 0;
                    while (rs1.next()) {
                        i++;
                    }
                    Stack s = new Stack(i);
                    PreparedStatement pst = con.prepareStatement(select);
                    ResultSet rs = pst.executeQuery();
                    while (rs.next()) {
                        ticketId = rs.getInt(1);
                        matchId = rs.getInt(2);
                        userId = rs.getInt(3);
                        stand = rs.getString(4);
                        ticketPrice = rs.getInt(5);
                        bookedTickets = rs.getInt(6);
                        totalPaid = rs.getInt(7);
                        payMethod = rs.getString(8);
                        TicketDetail td = new TicketDetail(ticketId, matchId, userId, stand, ticketPrice, bookedTickets, totalPaid, payMethod);
                        s.push(td);
                    }
                    System.out.println("----- Last " + n + " Ticket Details -----");
                    for (int j = 0; j < n; j++) {
                        if (!s.isEmpty()) {
                            System.out.println(s.pop());
                        } else {
                            System.out.println("No more tickets available.");
                            break;
                        }
                    }
                    break;
                }
                case 4:
                {
                    System.out.println("Enter Match id To Print Match Details : ");
                    int id = sc.nextInt();
                    String select = "select * from matches where match_id="+id;
                    PreparedStatement pst = con.prepareStatement(select);
                    ResultSet rs = pst.executeQuery();
                    int i =0;
                    while(rs.next()) {
                        i++;
                        System.out.println("------ Match Details ------");
                        System.out.println("Match Id : "+rs.getInt(1));
                        System.out.println("Match Name : "+rs.getString(2));
                        System.out.println("Series/Tournament Name : "+rs.getString(3));
                        System.out.println("Match Format : "+rs.getString(4));
                        System.out.println("Match Date : "+rs.getString(5));
                        System.out.println("Match Time : "+rs.getString(6));
                    }
                    if(i==0)
                    {
                        System.out.println("Match Not Found");
                    }
                    break;
                }
                case 5:
                {
                    System.out.println("Enter User id To Print User Details : ");
                    int id = sc.nextInt();
                    String select = "select * from user where user_id = "+id;
                    PreparedStatement pst = con.prepareStatement(select);
                    ResultSet rs = pst.executeQuery();
                    int i =0;
                    while(rs.next()) {
                        i++;
                        System.out.println("------ User Details ------");
                        System.out.println("User Id : "+rs.getInt(1));
                        System.out.println("Name : "+rs.getString(2));
                        System.out.println("Mobile No. : "+rs.getString(4));
                    }
                    if(i==0)
                    {
                        System.out.println("User Not Found");
                    }
                    break;
                }
                case 6:
                {
                    System.out.println("Enter Match ID to View Revenue Report:");
                    int matchId = sc.nextInt();
                    String sql = "SELECT stand, total_revenue FROM revenue WHERE match_id=?";
                    PreparedStatement pst = con.prepareStatement(sql);
                    pst.setInt(1, matchId);
                    ResultSet rs = pst.executeQuery();
                    System.out.println("===== Revenue Report for Match ID " + matchId + " =====");
                    double totalRevenue = 0;
                    while (rs.next()) {
                        String stand = rs.getString("stand");
                        double revenue = rs.getDouble("total_revenue");
                        System.out.println("Stand " + stand + " : Rs. " + revenue);
                        totalRevenue += revenue;
                    }
                    System.out.println("--------------------------------------");
                    System.out.println("Total Revenue from Match: Rs. " + totalRevenue);
                    rs.close();
                    pst.close();
                    break;
                }
                case 7:
                {
                    System.out.println("Enter Match ID to Generate Bills & Reports:");
                    int matchId = sc.nextInt();
                    String checkMatch = "SELECT match_id, status FROM matches WHERE match_id=?";
                    PreparedStatement pstCheck = con.prepareStatement(checkMatch);
                    pstCheck.setInt(1, matchId);
                    ResultSet rsCheck = pstCheck.executeQuery();
                    if (!rsCheck.next()) {
                        System.out.println("❌ Match ID not found.");
                        break;
                    }
                    String currentStatus = rsCheck.getString("status");
                    rsCheck.close();
                    pstCheck.close();
                    if (!"COMPLETED".equalsIgnoreCase(currentStatus)) {
                        System.out.println("⚠ Match is currently marked as '" + currentStatus + "'.");
                        System.out.println("Do you want to mark it as COMPLETED and generate reports? (yes/no): ");
                        String confirm = sc.next();
                        if (confirm.equalsIgnoreCase("yes")) {
                            String updateSql = "UPDATE matches SET status='COMPLETED' WHERE match_id=?";
                            PreparedStatement pstUpdate = con.prepareStatement(updateSql);
                            pstUpdate.setInt(1, matchId);
                            pstUpdate.executeUpdate();
                            pstUpdate.close();
                            System.out.println("✅ Match marked as COMPLETED.");
                        } else {
                            System.out.println("❌ Reports not generated because match is not completed.");
                            break;
                        }
                    }
                    else {
                        System.out.println("⚠ Match is already COMPLETED.");
                        System.out.println("Do you want to revert it back to UPCOMING? (yes/no): ");
                        String revert = sc.next();
                        if (revert.equalsIgnoreCase("yes")) {
                            String updateSql = "UPDATE matches SET status='UPCOMING' WHERE match_id=?";
                            PreparedStatement pstUpdate = con.prepareStatement(updateSql);
                            pstUpdate.setInt(1, matchId);
                            pstUpdate.executeUpdate();
                            pstUpdate.close();
                            System.out.println("✅ Match reverted to UPCOMING.");
                            break;
                        }
                        else {
                            System.out.println("✅ Proceeding with reports as match remains COMPLETED.");
                        }
                    }
                    System.out.println("What do you want to generate?");
                    System.out.println("1. Bills (Seat-wise Ticket Details)");
                    System.out.println("2. Revenue Report (Stand-wise Revenue)");
                    System.out.println("3. Both");
                    int ch = sc.nextInt();
                    if (ch == 1 || ch == 3) {
                        String ticketSql = "SELECT t.ticket_id, t.user_id, user_name, t.stand, t.no_of_tickets, t.total_payments, t.payment_method FROM ticket t JOIN user u ON t.user_id=u.user_id WHERE t.match_id=?";
                        PreparedStatement pstTicket = con.prepareStatement(ticketSql);
                        pstTicket.setInt(1, matchId);
                        ResultSet rsTicket = pstTicket.executeQuery();
                        System.out.println("\n===== Bills for Match ID " + matchId + " =====");
                        while (rsTicket.next()) {
                            System.out.println("Ticket ID: " + rsTicket.getInt("ticket_id"));
                            System.out.println("User ID: " + rsTicket.getInt("user_id") + " | Name: " + rsTicket.getString("user_name"));
                            System.out.println("Stand: " + rsTicket.getString("stand"));
                            System.out.println("Seats Booked: " + rsTicket.getInt("no_of_tickets"));
                            System.out.println("Total Paid: Rs. " + rsTicket.getInt("total_payments"));
                            System.out.println("Payment Method: " + rsTicket.getString("payment_method"));
                            System.out.println("--------------------------------------");
                        }
                        rsTicket.close();
                        pstTicket.close();
                    }
                    if (ch == 2 || ch == 3) {
                        String revSql = "SELECT stand, SUM(total_payments) AS total_revenue FROM ticket WHERE match_id=? GROUP BY stand";
                        PreparedStatement pstRev = con.prepareStatement(revSql);
                        pstRev.setInt(1, matchId);
                        ResultSet rsRev = pstRev.executeQuery();
                        System.out.println("\n===== Revenue Report for Match ID " + matchId + " =====");
                        double totalRevenue = 0;
                        while (rsRev.next()) {
                            String stand = rsRev.getString("stand");
                            double revenue = rsRev.getDouble("total_revenue");
                            System.out.println("Stand " + stand + " : Rs. " + revenue);
                            totalRevenue += revenue;
                        }
                        System.out.println("--------------------------------------");
                        System.out.println("Total Revenue from Match: Rs. " + totalRevenue);
                        rsRev.close();
                        pstRev.close();
                    }
                    break;
                }
                case 8:
                {
                    break;
                }
                default:
                {
                    System.out.println("Invalid Choice , Enter Valid Choice");
                    break;
                }
            }
        }
    }
}
class Ticket
{
    Scanner sc = new Scanner(System.in);
    Get g = new Get();
    void bookTicket(Connection con,int userId)throws Exception
    {
        int user_id = userId;
        int match_id;
        String stand = "";
        int ticket_price = 0;
        int no_of_ticket;
        double total_payments;
        String payment_method = "";
        LoginedUSer lu = new LoginedUSer();
        ArrayList<Integer> al = lu.getMatch(con);
        System.out.println("====== Welcome To Narendra Modi Stadium Booking Page ======");
        System.out.println("Enter Match Id From Above Match Details :");
        int matchId = sc.nextInt();
        if(al.contains(matchId)) {
            String statusSQL = "SELECT status FROM matches WHERE match_id = ?";
            PreparedStatement statusPST = con.prepareStatement(statusSQL);
            statusPST.setInt(1, matchId);
            ResultSet rsStatus = statusPST.executeQuery();
            if (rsStatus.next()) {
                String matchStatus = rsStatus.getString("status");
                if ("COMPLETED".equalsIgnoreCase(matchStatus)) {
                    System.out.println("❌ Ticket booking is not allowed for COMPLETED matches.");
                    rsStatus.close();
                    statusPST.close();
                    return;
                }
            }
            rsStatus.close();
            statusPST.close();
            match_id = matchId;
            System.out.println("  Stand_No.   |  Stand  |  Seat_Price  ");
            System.out.println("     1        |    A(lower)    |     4000    ");
            System.out.println("     2        |    B(lower)    |     2000    ");
            System.out.println("     3        |    C(lower)    |     2000    ");
            System.out.println("     4        |    D(lower)    |     2000    ");
            System.out.println("     5        |    E(lower)    |     2000    ");
            System.out.println("     6        |    F(lower)    |     2000    ");
            System.out.println("     7        |    G(lower)    |     2000    ");
            System.out.println("     8        |    H(lower)    |     4000    ");
            System.out.println("     9        |    J(upper)    |     800     ");
            System.out.println("     10       |    K(upper)    |     800     ");
            System.out.println("     11       |    L(lower)    |     800     ");
            System.out.println("     12       |    M(upper)    |     800     ");
            System.out.println("     13       |    N(upper)    |     800     ");
            System.out.println("     14       |    P(upper)    |     800     ");
            System.out.println("     15       |    Q(upper)    |     800     ");
            System.out.println("     16       |    R(upper)    |     800     ");
            System.out.println("Enter Stand No. From Table : ");
            int sNo = -1;
            int AT = 3;
            while(AT>0)
            {
                sNo = sc.nextInt();
                if(sNo>=1 && sNo<=16)
                {
                    break;
                }
                else
                {
                    AT--;
                    System.out.println("❌ Invalid Stand Number! Please enter between 1 and 16.");
                    if (AT > 0) {
                        System.out.println("Attempts left: " + AT);
                        System.out.print("Enter Stand No. again: ");
                    } else {
                        System.out.println("🚫 Maximum attempts reached. Returning to home page.");
                        return;
                    }
                }
            }
            switch (sNo) {
                case 1:
                    stand = "A";
                        ticket_price = 4000;
                        break;
                        case 2:
                        stand = "B";
                        ticket_price = 2000;
                        break;
                    case 3:
                        stand = "C";
                        ticket_price = 2000;
                        break;
                    case 4:
                        stand = "D";
                        ticket_price = 2000;
                        break;
                    case 5:
                        stand = "E";
                        ticket_price = 2000;
                        break;
                    case 6:
                        stand = "F";
                        ticket_price = 2000;
                        break;
                    case 7:
                        stand = "G";
                        ticket_price = 2000;
                        break;
                    case 8:
                        stand = "H";
                        ticket_price = 4000;
                        break;
                    case 9:
                        stand = "J";
                        ticket_price = 800;
                        break;
                    case 10:
                        stand = "K";
                        ticket_price = 800;
                        break;
                    case 11:
                        stand = "L";
                        ticket_price = 800;
                        break;
                    case 12:
                        stand = "M";
                        ticket_price = 800;
                        break;
                    case 13:
                        stand = "N";
                        ticket_price = 800;
                        break;
                    case 14:
                        stand = "P";
                        ticket_price = 800;
                        break;
                    case 15:
                        stand = "Q";
                        ticket_price = 800;
                        break;
                        case 16:
                        stand = "R";
                        ticket_price = 800;
                        break;
            }
            System.out.println("Enter No. Of Tickets You Want To Buy :");
            no_of_ticket = sc.nextInt();
            boolean baka = false;
            int totalSeats = 0, bookedSeats = 0;
            String select_availability = "select total_seats, booked_seats from seat_availability where match_id = ? and stand = ?";
            PreparedStatement pstmt_availability = con.prepareStatement(select_availability);
            pstmt_availability.setInt(1, match_id);
            pstmt_availability.setString(2, stand);
            ResultSet rs1_availability = pstmt_availability.executeQuery();
            if (rs1_availability.next()) {
                totalSeats = rs1_availability.getInt("total_seats");
                bookedSeats = rs1_availability.getInt("booked_seats");
            } else {
                String selectavailabiltiy = "select total_seats, booked_seats from seat_availability where match_id = ? AND stand=?";
                PreparedStatement pst_availability = con.prepareStatement(selectavailabiltiy);
                pst_availability.setInt(1, match_id);
                pst_availability.setString(2, stand);
                ResultSet rs2_availability = pst_availability.executeQuery();
                if (rs2_availability.next()) {
                    totalSeats = rs2_availability.getInt("total_seats");
                    bookedSeats = rs2_availability.getInt("booked_seats");
                    baka = true;
                } else {
                    totalSeats = (sNo <= 8) ? 200 : 200 ;
                    try {
                        String insert_availability = "insert into seat_availability(match_id, stand, total_seats, booked_seats) values(?,?,?,0)";
                        PreparedStatement insertPst = con.prepareStatement(insert_availability);
                        insertPst.setInt(1, match_id);
                        insertPst.setString(2, stand);
                        insertPst.setInt(3, totalSeats);
                        insertPst.executeUpdate();
                        insertPst.close();
                        bookedSeats = 0;
                    } catch (Exception available) {
                        String sl_availability = "select total_seats, booked_seats from seat_availability where stand=?";
                        PreparedStatement selectPst = con.prepareStatement(sl_availability);
                        selectPst.setString(1, stand);
                        ResultSet selectRs = selectPst.executeQuery();
                        if (selectRs.next()) {
                            totalSeats = selectRs.getInt("total_seats");
                            bookedSeats = selectRs.getInt("booked_seats");
                            baka = true;
                        } else {
                            throw available;
                        }
                        selectRs.close();
                        selectPst.close();
                    }
                }
                rs2_availability.close();
                pst_availability.close();
            }
            rs1_availability.close();
            pstmt_availability.close();
            int rows = 10, seatsPerRow = 20;
            System.out.println("\nSeat Map for Stand " + stand + " (✅=Available, ❌=Booked)");

            Set<String> booked = new HashSet<>();
            PreparedStatement pst_allocation = con.prepareStatement("SELECT row_no, seat_no FROM seat_allocation WHERE match_id=? AND stand=?");
            pst_allocation.setInt(1, matchId);
            pst_allocation.setString(2, stand);
            ResultSet Rs_allocation = pst_allocation.executeQuery();
            while (Rs_allocation.next()) {
                booked.add(Rs_allocation.getInt("row_no") + "-" + Rs_allocation.getInt("seat_no"));
            }
            for (int r = 1; r <= rows; r++) {
                System.out.print("Row " + r + ": ");
                for (int s = 1; s <= seatsPerRow; s++) {
                    String key = r + "-" + s;
                    if (booked.contains(key)) {
                        System.out.print("[" + s + "❌]");
                    } else {
                        System.out.print("[" + s + "✅]");
                    }
                }
                System.out.println();
            }
            System.out.println();
            List<String> bookedSeatsNo = new ArrayList<>();
            for (int i = 0; i < no_of_ticket; i++)
            {
                int attempt = 3;
                while (attempt>0) {
                    System.out.print("Enter Row : ");
                    int row = sc.nextInt();
                    System.out.print("Enter Seat Number: ");
                    int seatNo = sc.nextInt();
                    if(row<1||row>10)
                    {
                        attempt--;
                        System.out.println("❌ Invalid row number! Must be between 1 and 10.");
                        if (attempt > 0) System.out.println("Attempts left: " + attempt);
                        else {
                            System.out.println("🚫 Maximum attempts reached. Returning to home page.");
                            return;
                        }
                        continue;
                    }

                    if (seatNo < 1 || seatNo > 20) {
                        attempt--;
                        System.out.println("❌ Invalid seat number! Must be between 1 and 20.");
                        if (attempt > 0) System.out.println("Attempts left: " + attempt);
                        else {
                            System.out.println("🚫 Maximum attempts reached. Returning to home page.");
                            return;
                        }
                        continue;
                    }

                    PreparedStatement pst1_allocation = con.prepareStatement("SELECT * FROM seat_allocation WHERE match_id=? AND stand=? AND row_no=? AND seat_no=?");
                    pst1_allocation.setInt(1, matchId);
                    pst1_allocation.setString(2, stand);
                    pst1_allocation.setInt(3, row);
                    pst1_allocation.setInt(4, seatNo);
                    ResultSet Rs1_allocation = pst1_allocation.executeQuery();

                    if (!Rs1_allocation.next()) {
                        PreparedStatement insert_allocation = con.prepareStatement("INSERT INTO seat_allocation(match_id, stand, row_no, seat_no, user_id) VALUES (?,?,?,?,?)");
                        insert_allocation.setInt(1, matchId);
                        insert_allocation.setString(2, stand);
                        insert_allocation.setInt(3, row);
                        insert_allocation.setInt(4, seatNo);
                        insert_allocation.setInt(5, user_id);
                        insert_allocation.executeUpdate();

                        bookedSeatsNo.add("Row " + row + " Seat " + seatNo);
                        System.out.println("✅ Seat Row " + row + " Seat " + seatNo + " booked!");
                        break;
                    } else {
                        System.out.println("❌ Seat already booked! Try again.");
                    }
                }
            }
            String update = "UPDATE seat_availability SET booked_seats = booked_seats + ? WHERE match_id = ? AND stand = ?";
            PreparedStatement pstmt1 = con.prepareStatement(update);
            pstmt1.setInt(1, no_of_ticket);
            pstmt1.setInt(2,match_id);
            pstmt1.setString(3,stand);
            pstmt1.executeUpdate();
            System.out.println("Total Seats: "+totalSeats);
            int availableSeats = totalSeats - bookedSeats;
            System.out.println("Available Seats: "+availableSeats);
            System.out.println("Stand: "+stand);
            if (no_of_ticket > availableSeats) {
                System.out.println("❌ Sorry! Only " + availableSeats + " seats are available in stand " + stand);
                return;
            }
            total_payments = ticket_price*no_of_ticket;
            System.out.println("Ticket Price = "+ticket_price);
            System.out.println("Total Tickets = "+no_of_ticket);
            System.out.println("--------------------");
            System.out.println("Total Bill : "+total_payments);
            System.out.println("--------------------");
            System.out.println("-- Available Payment Methods --");
            System.out.println("1. UPI");
            System.out.println("2. Debit Card");
            System.out.println("3. Credit Card");
            System.out.println("4. Netbanking");
            System.out.println("Enter Your Payment Method No.");
            int pmNo=sc.nextInt();
            switch (pmNo) {
                case 1:
                    boolean success = false;
                    int attempts = 3;
                    while (attempts > 0) {
                        System.out.println("Enter your UPI ID (e.g. user@upi): ");
                        String upiId = sc.next();
                        if (!upiId.matches("^[a-zA-Z0-9._-]+@upi$")) {
                            System.out.println("❌ Invalid UPI ID format! Example: user@upi");
                            attempts--;
                            if (attempts > 0) System.out.println("Attempts left: " + attempts);
                            continue;
                        }
                        System.out.println("Enter UPI PIN (4 to 6 digits): ");
                        String upiPin = sc.next();
                        if (!upiPin.matches("\\d{4,6}")) {
                            System.out.println("❌ Invalid UPI PIN! Must be 4–6 digits.");
                            attempts--;
                            if (attempts > 0) System.out.println("Attempts left: " + attempts);
                            continue;
                        }
                        success = true;
                        break;
                    }
                    if (!success) {
                        System.out.println("\n🚫 All 3 attempts failed. Redirecting to home page...");
                        return; // Exit from the current method (e.g., bookTicket), and go back to menu
                    }
                payment_method = "UPI";
                    break;
                case 2:
                    boolean success1 = false;
                    int attempts1 = 3;
                    while (attempts1 > 0) {
                        System.out.println("Enter your 16-digit Debit Card Number: ");
                        String cardNumber = sc.next();
                        if (cardNumber.length() != 16 || !isAllDigits(cardNumber)) {
                            System.out.println("❌ Invalid card number! Must be exactly 16 digits.");
                            attempts1--;
                            if (attempts1 > 0) System.out.println("Attempts left: " + attempts1);
                            continue;
                        }
                        System.out.println("Enter Expiry Date (MM/YY): ");
                        String expiryDate = sc.next();
                        if (!isValidExpiryDate(expiryDate)) {
                            System.out.println("❌ Invalid expiry date! Format must be MM/YY and a valid month.");
                            attempts1--;
                            if (attempts1 > 0) System.out.println("Attempts left: " + attempts1);
                            continue;
                        }
                        System.out.println("Enter CVV (3 digits): ");
                        String cvv = sc.next();
                        if (cvv.length() != 3 || !isAllDigits(cvv)) {
                            System.out.println("❌ Invalid CVV! Must be exactly 3 digits.");
                            attempts1--;
                            if (attempts1 > 0) System.out.println("Attempts left: " + attempts1);
                            continue;
                        }
                        success1 = true;
                        break;
                    }
                    if (!success1) {
                        System.out.println("\n🚫 All 3 attempts failed. Redirecting to home page...");
                        return;
                    }
                    payment_method = "Debit Card";
                    break;
                case 3:
                    boolean success2 = false;
                    int attempts2 = 3;
                    while (attempts2 > 0) {
                        System.out.println("Enter your 16-digit Credit Card Number: ");
                        String cardNumber = sc.next();
                        if (cardNumber.length() != 16 || !isAllDigits(cardNumber)) {
                            System.out.println("❌ Invalid card number! Must be exactly 16 digits.");
                            attempts2--;
                            if (attempts2 > 0) System.out.println("Attempts left: " + attempts2);
                            continue;
                        }
                        System.out.println("Enter Expiry Date (MM/YY): ");
                        String expiryDate = sc.next();
                        if (!isValidExpiryDate(expiryDate)) {
                            System.out.println("❌ Invalid expiry date! Format must be MM/YY and a valid month.");
                            attempts2--;
                            if (attempts2 > 0) System.out.println("Attempts left: " + attempts2);
                            continue;
                        }
                        System.out.println("Enter CVV (3 digits): ");
                        String cvv = sc.next();
                        if (cvv.length() != 3 || !isAllDigits(cvv)) {
                            System.out.println("❌ Invalid CVV! Must be exactly 3 digits.");
                            attempts2--;
                            if (attempts2 > 0) System.out.println("Attempts left: " + attempts2);
                            continue;
                        }
                        success2 = true;
                        break;
                    }
                    if (!success2) {
                        System.out.println("\n🚫 All 3 attempts failed. Redirecting to home page...");
                        return;
                    }
                payment_method = "Credit Card";
                    break;
                case 4:
                    boolean success3 = false;
                    int attempts3 = 3;
                    while (attempts3 > 0) {
                        System.out.println("Enter your Bank Name: ");
                        String bankName = sc.nextLine();
                        if (bankName.isEmpty()) {
                            System.out.println("❌ Bank name cannot be empty.");
                            attempts3--;
                            if (attempts3 > 0) System.out.println("Attempts left: " + attempts3);
                            continue;
                        }
                        System.out.println("Enter your Netbanking User ID (no spaces): ");
                        String userId1 = sc.next();
                        if (userId1.isEmpty() || userId1.contains(" ")) {
                            System.out.println("❌ Invalid User ID! It cannot be empty or contain spaces.");
                            attempts3--;
                            if (attempts3 > 0) System.out.println("Attempts left: " + attempts3);
                            continue;
                        }
                        System.out.println("Enter your Netbanking Password (min 6 chars): ");
                        String password = sc.next();
                        if (password.length() < 6) {
                            System.out.println("❌ Password too short! Minimum 6 characters required.");
                            attempts3--;
                            if (attempts3 > 0) System.out.println("Attempts left: " + attempts3);
                            continue;
                        }
                        success3 = true;
                        break;
                    }
                    if (!success3)
                    {
                        System.out.println("\n🚫 All 3 attempts failed. Redirecting to home page...");
                        return;
                    }
                payment_method = "Netbanking";
                    break;
            }
            String select = "select mobile_no from user where user_id="+userId;
            PreparedStatement pst1 = con.prepareStatement(select);
            ResultSet rs = pst1.executeQuery();
            while (rs.next()) {
                int otp=(int)(Math.random()*1000000);
                String mob = rs.getString(1);
                File f = new File("src/"+mob+".txt");
                RandomAccessFile file = new RandomAccessFile(f,"rw");
                file.seek(file.length());
                file.writeBytes("\n"+g.getDateTime());
                file.writeBytes("\n1 new Message : ");
                file.writeBytes("\n   From Narendra Modi Stadium Web : ");
                file.writeBytes("\n   Total Payment For Book Ticket is "+total_payments+" Rs.");
                file.writeBytes("\n   Your OTP for Payment Confirmation is "+otp+"\n");
                System.out.println("OTP sent to Your Registered Mobile No. "+mob);
                int p=3;
                boolean b = false;
                while (p>0) {
                    System.out.println("Enter OTP To Confirm Your Payment : ");
                    int fOtp = sc.nextInt();
                    if (otp==fOtp) {
                        b = true;
                        System.out.print("Processing Payment");
                        Thread.sleep(1000);
                        System.out.print(".");
                        Thread.sleep(1000);
                        System.out.print(".");
                        Thread.sleep(1000);
                        System.out.println(".");
                        System.out.println("Payment Confirmed !!!");
                        System.out.print("Redirecting To The User Page , Please Wait");
                        Thread.sleep(1000);
                        System.out.print(".");
                        Thread.sleep(1000);
                        System.out.print(".");
                        Thread.sleep(1000);
                        System.out.println(".");
                        file.seek(file.length());
                        file.writeBytes("\n"+g.getDateTime());
                        file.writeBytes("\n1 new Message : ");
                        file.writeBytes("\n   From Your Bank : ");
                        file.writeBytes("\n   Rs. "+total_payments+" Debited From Bank Account Through "+payment_method+".\n");
                        file.seek(file.length());
                        file.writeBytes("\n"+g.getDateTime());
                        file.writeBytes("\n1 new Message : ");
                        file.writeBytes("\n   From Narendra Modi Stadium Web : ");
                        file.writeBytes("\n   Congratulation, Your Tickets Are Booked.");
                        file.writeBytes("\n   You Can SMS Ticket's Full Details From Narendra Modi Stadium Web.");
                        file.writeBytes("\n   Thank You.\n");
                        break;
                    }
                    else
                    {
                        System.out.println("Invalid OTP , Attempt left : "+--p);
                    }
                }
                if(b)
                {
                    String sql = "insert into ticket(match_id,user_id,stand,ticket_price,no_of_tickets,total_payments,payment_method) values (?,?,?,?,?,?,?)";
                    PreparedStatement pst = con.prepareStatement(sql);
                    pst.setInt(1, match_id);
                    pst.setInt(2, user_id);
                    pst.setString(3, stand);
                    pst.setInt(4, ticket_price);
                    pst.setInt(5, no_of_ticket);
                    pst.setDouble(6, total_payments);
                    pst.setString(7, payment_method);
                    int r = pst.executeUpdate();
                    if(r>0){}
                }
                else
                {
                    System.out.println("All Attempts Are Over, Payment Cancelled ");
                    System.out.println("Sorry Your Booking is Cancelled");
                }
                file.close();
            }
            String revenueUpdate = "INSERT INTO revenue (match_id, stand, total_revenue) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE total_revenue = total_revenue + ?";
            PreparedStatement revenueStmt = con.prepareStatement(revenueUpdate);
            revenueStmt.setInt(1, match_id);
            revenueStmt.setString(2, stand);
            revenueStmt.setDouble(3, total_payments);
            revenueStmt.setDouble(4, total_payments);
            revenueStmt.executeUpdate();
            revenueStmt.close();
        }
        else
        {
            System.out.println("Match Id Not Found , Redirecting To User Page.");
        }
    }

// Helper methods outside your main method/class:

    public static boolean isAllDigits(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }

    public static boolean isValidExpiryDate(String expiry) {
        // Check length MM/YY = 5 chars
        if (expiry.length() != 5) return false;
        // Check the slash at 3rd position
        if (expiry.charAt(2) != '/') return false;

        String monthStr = expiry.substring(0, 2);
        String yearStr = expiry.substring(3, 5);

        // Check month digits
        if (!isAllDigits(monthStr) || !isAllDigits(yearStr)) return false;

        int month = Integer.parseInt(monthStr);
        // Month must be between 1 and 12
        if (month < 1 || month > 12) return false;

        return true;
    }

    void smsTicket(Connection con,int userId) throws Exception
    {
        int ticketId;
        String stand = "";
        int ticketPrice;
        int bookedTickets;
        int totalPaid;
        int matchId;
        String payMethod = "";
        String userName = "";
        String mobile = "";
        String matchName = "";
        String matchDate = "";
        String matchTime = "";
        String select = "select * from ticket where user_id="+userId;
        PreparedStatement pst= con.prepareStatement(select);
        ResultSet rs = pst.executeQuery();
        int i = 0;
        while (rs.next()) {
            i++;
            matchId = rs.getInt(2);
            ticketId = rs.getInt(1);
            stand = rs.getString(4);
            ticketPrice = rs.getInt(5);
            bookedTickets = rs.getInt(6);
            totalPaid = rs.getInt(7);
            payMethod = rs.getString(8);
            String select1 = "select user_name,mobile_no from user where user_id="+userId;
            PreparedStatement pst1 =  con.prepareStatement(select1);
            ResultSet rs1 = pst1.executeQuery();
            while (rs1.next()) {
                userName = rs1.getString(1);
                mobile = rs1.getString(2);
            }
            String select2 = "select match_name,match_date,match_time from matches where match_id="+matchId;
            PreparedStatement pst2 =  con.prepareStatement(select2);
            ResultSet rs2 = pst2.executeQuery();
            while (rs2.next()) {
                matchName=rs2.getString(1);
                matchDate=rs2.getString(2);
                matchTime=rs2.getString(3);
            }
            File f =  new File("src/"+mobile+".txt");
            RandomAccessFile file = new RandomAccessFile(f,"rw");
            file.seek(file.length());
            file.writeBytes("\n"+g.getDateTime());
            file.writeBytes("\n1 new Message : ");
            file.writeBytes("\n   From Narendra Modi Stadium Web : ");
            file.writeBytes("\n       TICKET'S DETAILS  ");
            file.writeBytes("\n   Ticket ID : "+ticketId);
            file.writeBytes("\n   Ticket Booked By : "+userName);
            file.writeBytes("\n   Match Name : "+matchName);
            file.writeBytes("\n   Match Date : "+matchDate);
            file.writeBytes("\n   Match Time : "+matchTime);
            file.writeBytes("\n   Stand Name : "+stand);
            file.writeBytes("\n   Ticket Price : "+ticketPrice);
            file.writeBytes("\n   Booked Tickets : "+bookedTickets);
            file.writeBytes("\n   Total Amount Paid : "+totalPaid);
            file.writeBytes("\n   Method Of Payment : "+payMethod+"\n");
            System.out.println("Tickets Details Sent Successfully to Mobile No. : "+mobile);
            file.close();
        }
        if(i==0)
        {
            System.out.println("Not Any Tickets Booked By You, User ID : "+userId);
        }
    }
}
class TicketDetail
{
    int ticket_id;
    int match_id;
    int user_id;
    String stand;
    int ticket_price;
    int no_of_tickets;
    double total_payments;
    String payment_method;
    public TicketDetail(int ticket_id, int match_id, int user_id, String stand, int ticket_price, int no_of_tickets,
                        double total_payments, String payment_method) {
        this.ticket_id = ticket_id;
        this.match_id = match_id;
        this.user_id = user_id;
        this.stand = stand;
        this.ticket_price = ticket_price;
        this.no_of_tickets = no_of_tickets;
        this.total_payments = total_payments;
        this.payment_method = payment_method;
    }
    @Override
    public String toString() {
        return "Ticket ID : " + ticket_id + "\nMatch ID : " + match_id + "\nUser ID : " + user_id + "\nStand Name : "
                + stand + "\nTicket Price : " + ticket_price + "\nNo. Of Tickets Booked : " + no_of_tickets + "\nTotal Payment : "
                + total_payments + "\nPayment Method : " + payment_method + "\n";
    }
}
class Get
{
    String getDateTime()
    {
        String n;
        String s =new  SimpleDateFormat("dd/MM/yyyy").format(new Date());
        n ="Date - "+s+"      Time - "+new SimpleDateFormat("hh:mm:ss").format(new Date());
        return n;
    }
}
class Stack
{
    int top;
    TicketDetail[] td;
    Stack(int i)
    {
        td=new TicketDetail[i];
        top=-1;
    }
    void push(TicketDetail td1)
    {
        top++;
        td[top]=td1;
    }
    public boolean isEmpty()
    {
        return top == -1;
    }
    TicketDetail pop()
    {
        top--;
        return td[top+1];
    }
}
class LinkedList
{
    class Node
    {
        Match m;
        Node next;
        Node (Match m)
        {
            this.m=m;
            next=null;
        }
    }
    Node first=null;
    void add(Match m)
    {
        Node n = new Node(m);
        if(first==null)
        {
            first=n;
        }
        else
        {
            n.next = first;
            first = n;
        }
    }
    void display()
    {
        Node temp = first;
        while (temp != null) {
            System.out.println(temp.m);
            System.out.println("===========================================");
            temp = temp.next;
        }
    }
}