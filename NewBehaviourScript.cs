//WASD to orbit, left Ctrl/Alt to zoom
using UnityEngine;

[AddComponentMenu("Camera-Control/Keyboard Orbit")]

public class NewBehaviourScript : MonoBehaviour
{
    public Transform target;

    float xSpeed = 2f;
    float ySpeed = 2f;
    float zoomSpd = 2f;
    float maxzoom = 1f;
    float minzoom = 100;
    public void Start()
    {

    }

    public void LateUpdate()
    {
        if (target)
        {
            float x = Input.GetAxis("Horizontal") * xSpeed * 0.02f;
            float y = Input.GetAxis("Vertical") * ySpeed * 0.02f;

            float distance =  Input.GetAxis("Fire1") * zoomSpd * 0.02f;
            distance -= Input.GetAxis("Fire2") * zoomSpd * 0.02f;
            float zoom = transform.position.y + distance;
            if (zoom > minzoom)
                zoom = minzoom;
            if (zoom < maxzoom)
                zoom = maxzoom;


            transform.position = new Vector3(transform.position.x + x, zoom, transform.position.z + y);
                
        }
    }


}