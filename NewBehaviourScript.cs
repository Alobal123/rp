//WASD to orbit, left Ctrl/Alt to zoom
using UnityEngine;

[AddComponentMenu("Camera-Control/Keyboard Orbit")]

public class NewBehaviourScript : MonoBehaviour
{
    public Transform target;

    float xSpeed = 2f;
    float ySpeed = 2f;
    float zoomSpd = 2f;
    float maxzoom = 50f;
    float minzoom = 300;
    public void Start()
    {

    }

    public void LateUpdate()
    {
        if (target)
        {
            float x = Input.GetAxis("Horizontal") * xSpeed;
            float y = Input.GetAxis("Vertical") * ySpeed ;

            float distance =  Input.GetAxis("Fire1") * zoomSpd;
            distance -= Input.GetAxis("Fire2") * zoomSpd ;
            float zoom = transform.position.y + distance;
            if (zoom > minzoom)
                zoom = minzoom;
            if (zoom < maxzoom)
                zoom = maxzoom;


            transform.position = new Vector3(transform.position.x + x, zoom, transform.position.z + y);
                
        }
    }


}