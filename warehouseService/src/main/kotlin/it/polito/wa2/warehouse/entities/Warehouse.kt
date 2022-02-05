package it.polito.wa2.warehouse.entities

import it.polito.wa2.warehouse.dto.WarehouseDTO
import javax.persistence.*


@Entity
class Warehouse(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    val id : Long? = null,

    val name: String?,
    val location: String?,

   @OneToMany(mappedBy = "warehouse")
    val availabilities: MutableList<ProductAvailability> = mutableListOf()



){
    //: EntityBase<Long>(){

    fun toDTO(): WarehouseDTO {
        //return WarehouseDTO(id!!, name!!, location!! )
        var map=  mutableMapOf<Long, Int>()
        for(a in availabilities){
            map.put(a.product.id!!, a.quantity)
        }
        return WarehouseDTO(id!!, name!!, location!!, map!!)
    }



}