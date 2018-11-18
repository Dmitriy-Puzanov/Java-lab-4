import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class StaffDemo {

    public static void main(String[] args) {

        //�������
        HashMap<String, Project> Projects = new HashMap<>();
        Projects.put("���", new Project("���", 6600000));
        Projects.put("������", new Project("������", 8000000));

        int SummProjects = 0;

        for(String mProjects: Projects.keySet()){
            SummProjects += Projects.get(mProjects).GetSumm();
        }

        //���������
        HashMap<String, Position> Positions = new HashMap<>();
        Positions.put("��������", new Position("��������", true, false, (byte) 0));
        Positions.put("��������", new Position("��������", true, false, (byte) 0));
        Positions.put("�����������", new Position("�����������", true, true, (byte) 1));
        Positions.put("�����������", new Position("�����������", true, true, (byte) 0.7));
        Positions.put("������� �����������", new Position("������� �����������", true, true, (byte) 1.2));
        Positions.put("��������", new Position("��������", false, true, (byte) 1.6));
        Positions.put("��������� ��������", new Position("��������� ��������", false, true, (byte) 4));
        Positions.put("������������ �����������", new Position("������������ �����������", false, false, (byte) 0));

        ArrayList<?> Staff = new ArrayList<>();

        Download(Staff, Projects, Positions);

        int min = 100;
        int max = 160;
        Random rnd = new Random(System.currentTimeMillis());

        Iterator it = Staff.iterator();
        while (it.hasNext()) {

            int mWorktime = min + rnd.nextInt(max - min + 1);

            Object my = it.next();


            try {
                Class[] paramTypes = new Class[]{int.class};
                Method ChangeWorkTime = my.getClass().getMethod("ChangeWorkTime", paramTypes);
                ChangeWorkTime.invoke(my, mWorktime);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {}

            try {
                Method �alculationPayment = my.getClass().getMethod("�alculationPayment");
                �alculationPayment.invoke(my);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {}

            try {
                Method �alculationProject = my.getClass().getMethod("�alculationProject");
                �alculationProject.invoke(my);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {}

            try {
                Class[] paramTypes = new Class[]{int.class};
                Method �alculationHeading = my.getClass().getMethod("�alculationHeading", paramTypes);
                �alculationHeading.invoke(my, SummProjects);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {}

            Employee emp = (Employee)my;

            System.out.println(emp.name + ": " + emp.payment);

        }
    }

    static void Download(ArrayList Staff, HashMap<String, Project> Projects, HashMap<String, Position> Positions) {

        try {
            File f = new File("DownloadStaff.json");
            JSONParser parser = new JSONParser();
            FileReader fr = new FileReader(f);
            Object obj = parser.parse(fr);
            JSONObject js = (JSONObject) obj;

            JSONArray staffs = (JSONArray) js.get("Staff");
            for (Object i : staffs) {

                String mName = ((JSONObject) i).get("fio").toString();
                String mPosition = ((JSONObject) i).get("position").toString();
                String mProject = ((JSONObject) i).get("project").toString();
                int mID = Integer.parseInt(((JSONObject) i).get("id").toString());
                int mBase = Integer.parseInt(((JSONObject) i).get("base").toString());

                Project StaffProject = Projects.get(mProject);
                Position StaffPosition = Positions.get(mPosition);

                switch (mPosition) {
                    case "��������":
                        Staff.add(new Driver(mID, mName, StaffPosition, mBase));
                        break;
                    case "��������":
                        Staff.add(new Cleaner(mID, mName, StaffPosition, mBase));
                        break;
                    case "�����������":
                        Staff.add(new Programmer(mID, mName, StaffPosition, mBase, StaffProject));
                        break;
                    case "�����������":
                        Staff.add(new Tester(mID, mName, StaffPosition, mBase, StaffProject));
                        break;
                    case "������� �����������":
                        Staff.add(new TeamLeader(mID, mName, StaffPosition, mBase, StaffProject));
                        break;
                    case "��������":
                        Staff.add(new Manager(mID, mName, StaffPosition, StaffProject));
                        break;
                    case "��������� ��������":
                        Staff.add(new ProjectManager(mID, mName, StaffPosition, StaffProject));
                        break;
                    case "������������ �����������":
                        Staff.add(new SeniorManager(mID, mName, StaffPosition, StaffProject));
                        break;
                }

                if (StaffProject != null) {
                    if (StaffPosition.ItPosition()) {
                        StaffProject.AddITStaff();
                    } else {
                        StaffProject.AddManagerStaff();
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

class Project {

    private String name;
    private int summ;
    private int itstaff;
    private int managerstaff;

    Project(String mName, int mSumm) {
        summ = mSumm;
        name = mName;
    }

    int GetSumm() {
        return summ;
    }

    int GetITStaff() {
        return itstaff;
    }

    int GetManagerStaff() {
        return managerstaff;
    }

    public void AddITStaff() {
        itstaff++;
    }

    public void AddManagerStaff() {
        managerstaff++;
    }
}

class Position {
    private String name;
    private boolean salary;
    private boolean project;
    private byte percentproject;

    Position(String mName, boolean mSalary, boolean mProject, byte mPercentProject) {
        name = mName;
        salary = mSalary;
        project = mProject;
        percentproject = mPercentProject;
    }

    byte GetPercentProject() {
        return (byte) (percentproject);
    }

    public boolean ItPosition() {
        return salary & project;
    }
}

abstract class Employee {

    protected int id;
    protected String name;
    protected int worktime;
    protected int payment;
    protected Position position;

    public Employee(int mId, String mName, Position mPosition) {
        id = mId;
        name = mName;
        position = mPosition;
    }
}

abstract class Pesonal extends Employee implements intrWorkTime {

    int base;

    public Pesonal(int mId, String mName, Position mPosition, int mbase) {
        super(mId, mName, mPosition);
        base = mbase;
    }

    public void ChangeWorkTime(int mWorktime) {
        worktime = mWorktime;
    }

    public void �alculationPayment() {
        payment += base * worktime;
    }
}

class Cleaner extends Pesonal {

    public Cleaner(int mId, String mName, Position mPosition, int mbase) {
        super(mId, mName, mPosition, mbase);
    }
}

class Driver extends Pesonal {

    public Driver(int mId, String mName, Position mPosition, int mbase) {
        super(mId, mName, mPosition, mbase);
    }
}

abstract class Engineer extends Employee implements intrWorkTime, intrProject {

    Project project;
    int base;

    public Engineer(int mId, String mName, Position mPosition, int mbase, Project mProject) {
        super(mId, mName, mPosition);
        base = mbase;
        project = mProject;
    }

    public void ChangeWorkTime(int mWorktime) {
        worktime = mWorktime;
    }

    public void �alculationPayment() {
        payment += base * worktime;
    }

    public void �alculationProject() {
        payment += (project.GetSumm() * position.GetPercentProject()) / 100;
    }
}

class Programmer extends Engineer {

    public Programmer(int mId, String mName, Position mPosition, int mbase, Project mProject) {
        super(mId, mName, mPosition, mbase, mProject);
    }
}

class Tester extends Engineer {

    public Tester(int mId, String mName, Position mPosition, int mbase, Project mProject) {
        super(mId, mName, mPosition, mbase, mProject);
    }
}

class TeamLeader extends Engineer implements intrHeading {

    public TeamLeader(int mId, String mName, Position mPosition, int mbase, Project mProject) {
        super(mId, mName, mPosition, mbase, mProject);
    }

    public void �alculationHeading(int SummProjects) {
        payment += project.GetSumm()* (project.GetITStaff()-1 * position.GetPercentProject()*0.1)/100;
    }
}

class Manager extends Employee implements intrProject{

    Project project;

    Manager(int mId, String mName, Position mPosition, Project mProject) {
        super(mId, mName, mPosition);
        project = mProject;
    }

    public void �alculationProject() {
        payment += (project.GetSumm() * position.GetPercentProject()) / 100;
    }
}

class ProjectManager extends Manager implements intrHeading{

    ProjectManager(int mId, String mName, Position mPosition, Project mProject) {
        super(mId, mName, mPosition, mProject);
    }

    public void �alculationHeading(int SummProjects) {
        payment += project.GetSumm()* (project.GetManagerStaff()-1 * position.GetPercentProject()*0.06)/100;
    }
}

class SeniorManager extends ProjectManager{

    SeniorManager(int mId, String mName, Position mPosition, Project mProject) {
        super(mId, mName, mPosition, mProject);
    }

    public void �alculationHeading(int SummProjects) {
        payment += SummProjects * 0.056;
    }
}

interface intrWorkTime {

    void ChangeWorkTime(int mWorktime);

    void �alculationPayment();

}

interface intrProject {
    void �alculationProject();
}

interface intrHeading {
    void �alculationHeading(int SummProjects);
}