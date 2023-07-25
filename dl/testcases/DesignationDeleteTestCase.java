import com.thinking.machines.hr.dl.exceptions.*;
import com.thinking.machines.hr.dl.interfaces.dao.*;
import com.thinking.machines.hr.dl.dao.*;

public class DesignationDeleteTestCase {
    public static void main(String gg[]) {
        int code = Integer.parseInt(gg[0]);
        try {
            DesignationDAOInterface designationDAO;
            designationDAO = new DesignationDAO();
            designationDAO.delete(code);
            System.out.println("Designation deleted with code as : " + code);
        } catch (DAOException daoException) {
            System.out.println(daoException.getMessage());
        }
    }
}