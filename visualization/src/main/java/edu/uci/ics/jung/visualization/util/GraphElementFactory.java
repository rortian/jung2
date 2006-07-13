package edu.uci.ics.jung.visualization.util;


public interface GraphElementFactory<V, E> {
    
    E createDirectedEdge(V v, V u);
    E createUndirectedEdge(V v, V u);
    V createVertex();
    
}

//    public static <V> V createVertex(Class<V> c, V obj) {
//        try {
//            Constructor<V> ctor =  c.getConstructor(c);
//            return ctor.newInstance(obj);
//            
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return null;
//        }
//    }
//
//    public static <V> V createVertex(Class<V> c) {
//        try {
//            Constructor<V> ctor =  c.getConstructor(c);
//            return ctor.newInstance();
//            
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return null;
//        }
//    }
//
////    public static Edge createEdge(Class c, Object v, Object u) {
////        try {
////            Constructor ctor =  c.getConstructor(v.getClass(), u.getClass());
////            return (Edge)ctor.newInstance(v,u);
////            
////        } catch (Exception ex) {
////            // TODO Auto-generated catch block
////            ex.printStackTrace();
////            return null;
////        }
////    }
//
//    public static <V,E extends Edge<V>> Edge createEdge(Class<E> c, V v, V u) {
//        try {
//            Constructor<E> ctor =  c.getConstructor(v.getClass(), u.getClass());
//            return ctor.newInstance(v,u);
//            
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return null;
//        }
//    }
//}
