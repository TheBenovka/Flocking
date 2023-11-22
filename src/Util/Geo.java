/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Util;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import java.util.concurrent.atomic.AtomicReference;

//Quelle Code von @penxon auf Discord
/**
 *
 * @author PauL
 */
public class Geo {
    public static Geometry getFirstGeometry(Spatial spatial) {
        if( spatial instanceof Geometry ) {
            return (Geometry)spatial;
        } else if( !(spatial instanceof Node) ) {
            return null;
        }
        
        final AtomicReference<Geometry> result = new AtomicReference<>();
        Node node = (Node)spatial;
        node.depthFirstTraversal(new SceneGraphVisitorAdapter(){
                @Override
                public void visit( Geometry geom ) {
                    if( result != null ) {
                        result.set(geom);
                    }
                }
            });

        return result.get();
    }
}
