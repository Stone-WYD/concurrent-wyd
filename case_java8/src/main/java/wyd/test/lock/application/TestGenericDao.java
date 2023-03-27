package wyd.test.lock.application;

import wyd.test.lock.application.GenericDao;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TestGenericDao {
    public static void main(String[] args) {
        GenericDao dao = new GenericDaoCached();

        System.out.println("==========> 查询");
        String sql = "select * from emp where empno = ?";
        int empno = 7369;
        Emp emp = dao.queryOne(Emp.class, sql, empno);
        System.out.println(emp);
        emp = dao.queryOne(Emp.class, sql, empno);
        System.out.println(emp);
        emp = dao.queryOne(Emp.class, sql, empno);
        System.out.println(emp);

        System.out.println("============> 更新");
        dao.update("update emp set sal = ? where empno = ?", 800, empno);
        emp = dao.queryOne(Emp.class, sql, empno);
        System.out.println(emp);
    }
}

// 使用装饰器模式
class GenericDaoCached extends GenericDao{

    private GenericDao dao = new GenericDao();
    private Map<SqlPair, Object> map = new HashMap<>();
    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();

    @Override
    public <T> List<T> queryList(Class<T> beanClass, String sql, Object... args) {
        return super.queryList(beanClass, sql, args);
    }

    @Override
    public <T> T queryOne(Class<T> beanClass, String sql, Object... args) {
        SqlPair sqlPair = new SqlPair(sql, args);
        rw.readLock().lock();
        try {
            T value = (T) map.get(sqlPair);
            if (value != null) {
                return value;
            }
        } finally {
            rw.readLock().unlock();
        }

        // 从缓存中没有获取到数据，则重新去数据库查找
        rw.writeLock().lock();
        try {
            T value = (T) map.get(sqlPair);
            // 双重检查
            if (value == null){
                value = dao.queryOne(beanClass, sql, args);
                map.put(sqlPair, value);
            }
            return value;
        } finally {
            rw.writeLock().unlock();
        }
    }

    @Override
    public int update(String sql, Object... args) {
        rw.writeLock().lock();
        try {
            // 先更新库
            int update = dao.update(sql, args);
            // 清空缓存
            map.clear();
            return update;
        } finally {
            rw.writeLock().unlock();
        }
    }

    class SqlPair{
        private String sql;
        private Object[] args;

        public SqlPair(String sql, Object[] args) {
            this.sql = sql;
            this.args = args;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SqlPair sqlPair = (SqlPair) o;
            return Objects.equals(sql, sqlPair.sql) && Arrays.equals(args, sqlPair.args);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(sql);
            result = 31 * result + Arrays.hashCode(args);
            return result;
        }
    }
}
