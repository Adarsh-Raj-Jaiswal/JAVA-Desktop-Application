import com.thinking.machines.hr.dl.exceptions.*;
import com.thinking.machines.hr.dl.interfaces.dao.*;
import com.thinking.machines.hr.dl.dao.*;

public class DesignationGetCountTestCase {
    public static void main(String gg[]) {
        try {
            DesignationDAOInterface designationDAO;
            designationDAO = new DesignationDAO();
            System.out.println("Count is : " + designationDAO.getCount());
        } catch (DAOException daoException) {
            System.out.println(daoException.getMessage());
        }
    }
}